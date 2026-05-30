package com.notifymesh.templateservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import static com.notifymesh.templateservice.constants.TemplateFields.CHANNEL_TYPE;
import static com.notifymesh.templateservice.constants.TemplateFields.CREATED_DATE;
import static com.notifymesh.templateservice.constants.TemplateFields.TEMPLATE_NAME;
import static com.notifymesh.templateservice.constants.TemplateFields.VERSION;

@Component
@RequiredArgsConstructor
public class DynamoDbTableInitializer implements ApplicationRunner {

    private static final String GSI_NAME = "channelType-createdDate-index";
    private static final long PROVISIONED_RCU = 5L;
    private static final long PROVISIONED_WCU = 5L;

    private final DynamoDbClient dynamoDbClient;
    private final AwsProperties awsProperties;

    @Override
    public void run(ApplicationArguments args) {
        String tableName = awsProperties.getDynamodb().getTableName();
        if (!tableExists(tableName)) {
            createTable(tableName);
        }
    }

    private boolean tableExists(String tableName) {
        try {
            dynamoDbClient.describeTable(r -> r.tableName(tableName));
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    private void createTable(String tableName) {
        ProvisionedThroughput throughput = ProvisionedThroughput.builder()
                .readCapacityUnits(PROVISIONED_RCU)
                .writeCapacityUnits(PROVISIONED_WCU)
                .build();

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName(TEMPLATE_NAME)
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName(VERSION)
                                .attributeType(ScalarAttributeType.N)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName(CHANNEL_TYPE)
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName(CREATED_DATE)
                                .attributeType(ScalarAttributeType.S)
                                .build()
                )
                .keySchema(
                        KeySchemaElement.builder()
                                .attributeName(TEMPLATE_NAME)
                                .keyType(KeyType.HASH)
                                .build(),
                        KeySchemaElement.builder()
                                .attributeName(VERSION)
                                .keyType(KeyType.RANGE)
                                .build()
                )
                .globalSecondaryIndexes(
                        GlobalSecondaryIndex.builder()
                                .indexName(GSI_NAME)
                                .keySchema(
                                        KeySchemaElement.builder()
                                                .attributeName(CHANNEL_TYPE)
                                                .keyType(KeyType.HASH)
                                                .build(),
                                        KeySchemaElement.builder()
                                                .attributeName(CREATED_DATE)
                                                .keyType(KeyType.RANGE)
                                                .build()
                                )
                                .projection(Projection.builder()
                                        .projectionType(ProjectionType.ALL)
                                        .build())
                                .provisionedThroughput(throughput)
                                .build()
                )
                .billingMode(BillingMode.PROVISIONED)
                .provisionedThroughput(throughput)
                .build();

        dynamoDbClient.createTable(request);

        try (DynamoDbWaiter waiter = dynamoDbClient.waiter()) {
            waiter.waitUntilTableExists(r -> r.tableName(tableName));
        }
    }
}
