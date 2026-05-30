package com.notifymesh.templateservice.infrastructure.repository;

import com.notifymesh.templateservice.config.AwsProperties;
import com.notifymesh.templateservice.domain.valueobject.Channel;
import com.notifymesh.templateservice.infrastructure.entity.Template;
import com.notifymesh.templateservice.mapper.TemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.notifymesh.templateservice.constants.TemplateFields.CHANNEL_TYPE;
import static com.notifymesh.templateservice.constants.TemplateFields.IS_ACTIVE;
import static com.notifymesh.templateservice.constants.TemplateFields.TEMPLATE_NAME;
import static com.notifymesh.templateservice.constants.TemplateFields.VERSION;

@Repository
@RequiredArgsConstructor
public class TemplateRepository {

    private static final String ATTR_VAL_TEMPLATE_NAME = ":templateName";

    private static final String ATTR_VAL_CHANNEL_TYPE = ":channelType";

    private static final String KEY_COND_TEMPLATE_NAME = TEMPLATE_NAME + " = :templateName";

    private static final String FILTER_CHANNEL_TYPE = CHANNEL_TYPE + " = :channelType";

    private final DynamoDbClient dynamoDbClient;

    private final AwsProperties awsProperties;

    public Optional<Template> findByTemplateNameAndChannelType(String templateName, Channel channelType) {
        QueryRequest request = QueryRequest.builder()
                .tableName(awsProperties.getDynamodb().getTableName())
                .keyConditionExpression(KEY_COND_TEMPLATE_NAME)
                .filterExpression(FILTER_CHANNEL_TYPE)
                .expressionAttributeValues(Map.of(
                        ATTR_VAL_TEMPLATE_NAME, AttributeValue.builder().s(templateName).build(),
                        ATTR_VAL_CHANNEL_TYPE, AttributeValue.builder().s(channelType.name()).build()
                ))
                .scanIndexForward(false)
                .build();

        QueryResponse response = dynamoDbClient.query(request);
        return response.items().stream()
                .findFirst()
                .map(TemplateMapper::fromItem);
    }

    public Optional<Template> findByTemplateNameAndChannelTypeAndVersion(String templateName, Channel channelType, Integer version) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(awsProperties.getDynamodb().getTableName())
                .key(Map.of(
                        TEMPLATE_NAME, AttributeValue.builder().s(templateName).build(),
                        VERSION, AttributeValue.builder().n(String.valueOf(version)).build()
                ))
                .build();

        GetItemResponse response = dynamoDbClient.getItem(request);
        if (!response.hasItem() || response.item().isEmpty()) {
            return Optional.empty();
        }

        Template template = TemplateMapper.fromItem(response.item());
        if (template.getChannelType() != channelType) {
            return Optional.empty();
        }

        return Optional.of(template);
    }

    @Retryable(
            retryFor = ProvisionedThroughputExceededException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public void save(Template template) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName(awsProperties.getDynamodb().getTableName())
                .item(TemplateMapper.toItem(template))
                .build();

        dynamoDbClient.putItem(request);
    }

    @Retryable(
            retryFor = ProvisionedThroughputExceededException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public void softDelete(String templateName, Channel channelType) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(awsProperties.getDynamodb().getTableName())
                .keyConditionExpression(KEY_COND_TEMPLATE_NAME)
                .filterExpression(FILTER_CHANNEL_TYPE)
                .expressionAttributeValues(Map.of(
                        ATTR_VAL_TEMPLATE_NAME, AttributeValue.builder().s(templateName).build(),
                        ATTR_VAL_CHANNEL_TYPE, AttributeValue.builder().s(channelType.name()).build()
                ))
                .scanIndexForward(false)
                .build();

        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);
        if (queryResponse.items().isEmpty()) {
            return;
        }

        Template latest = TemplateMapper.fromItem(queryResponse.items().get(0));

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(TEMPLATE_NAME, AttributeValue.builder().s(templateName).build());
        key.put(VERSION, AttributeValue.builder().n(String.valueOf(latest.getVersion())).build());

        String attrValInactive = ":inactive";
        String updateExpr = "SET " + IS_ACTIVE + " = " + attrValInactive;

        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName(awsProperties.getDynamodb().getTableName())
                .key(key)
                .updateExpression(updateExpr)
                .expressionAttributeValues(Map.of(
                        attrValInactive, AttributeValue.builder().bool(false).build()
                ))
                .build();

        dynamoDbClient.updateItem(updateRequest);
    }
}
