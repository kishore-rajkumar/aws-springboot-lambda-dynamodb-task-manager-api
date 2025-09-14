package com.kishore.taskmanager.integration;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.localstack.LocalStackContainer;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@SpringBootTest(classes = {DynamoDbClient.class})
@Import(LocalStackTestConfig.class)
@ActiveProfiles("test")
public abstract class BaseDynamoDbIntegrationTest  {

    protected DynamoDbClient dynamoDbClient;
    protected DynamoDbEnhancedClient enhancedClient;

    @BeforeAll
    void initDynamoDbClient() {
        LocalStackContainer localstack = LocalStackTestConfig.getContainer();

        dynamoDbClient = DynamoDbClient.builder()
            .endpointOverride(localstack.getEndpointOverride(DYNAMODB))
            .region(Region.of(localstack.getRegion()))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create("accesskey", "secretkey")
            ))
            .build();

        enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    }

    protected void createTasksTable() {
        dynamoDbClient.createTable(CreateTableRequest.builder()
            .tableName("Tasks")
            .keySchema(KeySchemaElement.builder()
                .attributeName("id")
                .keyType(KeyType.HASH)
                .build())
            .attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName("id")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("status")
                    .attributeType(ScalarAttributeType.S)
                    .build()
            )
            .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                .indexName("status-index")
                .keySchema(KeySchemaElement.builder()
                    .attributeName("status")
                    .keyType(KeyType.HASH)
                    .build())
                .projection(Projection.builder()
                    .projectionType(ProjectionType.ALL)
                    .build())
                .build())
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .build());
    }
}
