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

import static com.notifymesh.templateservice.constant.TemplateFields.CHANNEL_TYPE;
import static com.notifymesh.templateservice.constant.TemplateFields.IS_ACTIVE;
import static com.notifymesh.templateservice.constant.TemplateFields.TEMPLATE_NAME;
import static com.notifymesh.templateservice.constant.TemplateFields.VERSION;

@Repository
@RequiredArgsConstructor
public class TemplateRepository {

    private final DynamoDbClient dynamoDbClient;

    private final AwsProperties awsProperties;

    public Optional<Template> findByTemplateNameAndChannelType(String templateName, Channel channelType) {
        QueryRequest request = QueryRequest.builder()
                .tableName(awsProperties.getDynamodb().getTableName())
                .keyConditionExpression(TEMPLATE_NAME + " = :templateName")
                .filterExpression(CHANNEL_TYPE + " = :channelType")
                .expressionAttributeValues(Map.of(
                        ":templateName", AttributeValue.builder().s(templateName).build(),
                        ":channelType", AttributeValue.builder().s(channelType.name()).build()
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
                .keyConditionExpression(TEMPLATE_NAME + " = :templateName")
                .filterExpression(CHANNEL_TYPE + " = :channelType")
                .expressionAttributeValues(Map.of(
                        ":templateName", AttributeValue.builder().s(templateName).build(),
                        ":channelType", AttributeValue.builder().s(channelType.name()).build()
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

        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName(awsProperties.getDynamodb().getTableName())
                .key(key)
                .updateExpression("SET " + IS_ACTIVE + " = :inactive")
                .expressionAttributeValues(Map.of(
                        ":inactive", AttributeValue.builder().bool(false).build()
                ))
                .build();

        dynamoDbClient.updateItem(updateRequest);
    }
}
