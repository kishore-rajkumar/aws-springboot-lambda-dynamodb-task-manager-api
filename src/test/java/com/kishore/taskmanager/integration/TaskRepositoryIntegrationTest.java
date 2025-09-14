package com.kishore.taskmanager.integration;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.localstack.LocalStackContainer;

import com.kishore.taskmanager.TestAwsConfig;
import com.kishore.taskmanager.model.Task;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
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

@SpringBootTest(classes = {TestAwsConfig.class,DynamoDbClient.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(LocalStackTestConfig.class)
@ActiveProfiles("test")
public class TaskRepositoryIntegrationTest {

    private DynamoDbClient dynamoDbClient;
    private DynamoDbTable<Task> taskTable;

    @BeforeAll
    void setup() {
    	LocalStackContainer localstack = LocalStackTestConfig.getContainer();
    	
    	dynamoDbClient = DynamoDbClient.builder()
    	        .endpointOverride(localstack.getEndpointOverride(DYNAMODB))
    	        .region(Region.of(localstack.getRegion().toString()))
    	        .credentialsProvider(StaticCredentialsProvider.create(
    	            AwsBasicCredentials.create("accesskey", "secretkey")
    	        ))
    	        .build();
        
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

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

        taskTable = enhancedClient.table("Tasks", TableSchema.fromBean(Task.class));
    }

    @Test
    void testSaveAndRetrieveTask() {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle("Integration Test");
        task.setDescription("Testing with LocalStack");

        taskTable.putItem(task);

        Task retrieved = taskTable.getItem(Key.builder().partitionValue(task.getId()).build());
        
        System.out.println("Task retrieved - " + retrieved );
       

        assertNotNull(retrieved);
        assertEquals(task.getTitle(), retrieved.getTitle());
        assertEquals(task.getDescription(), retrieved.getDescription());
    }
    
    @Test
    void testUpdateTask() {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle("Original Title");
        task.setDescription("Original Description");

        taskTable.putItem(task);

        // Update the task
        task.setTitle("Updated Title");
        task.setDescription("Updated Description");
        taskTable.putItem(task);

        Task updated = taskTable.getItem(Key.builder().partitionValue(task.getId()).build());

        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Description", updated.getDescription());
    }
    
    @Test
    void testDeleteTask() {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle("To Be Deleted");
        task.setDescription("This will be removed");

        taskTable.putItem(task);

        taskTable.deleteItem(task);

        Task deleted = taskTable.getItem(Key.builder().partitionValue(task.getId()).build());

        assertNull(deleted);
    }
    
    @Test
    void testRetrieveMissingTask() {
        String fakeId = UUID.randomUUID().toString();

        Task result = taskTable.getItem(Key.builder().partitionValue(fakeId).build());

        assertNull(result);
    }
    
    @Test
    void testScanAllTasks() {
        // Insert multiple tasks
        for (int i = 1; i <= 3; i++) {
            Task task = new Task();
            task.setId(UUID.randomUUID().toString());
            task.setTitle("Task " + i);
            task.setDescription("Description " + i);
            taskTable.putItem(task);
        }

        // Scan the table
        List<Task> tasks = new ArrayList<>();
        taskTable.scan().items().forEach(tasks::add);

        // Assert that at least 3 tasks are present
        assertTrue(tasks.size() >= 3);
        tasks.forEach(t -> System.out.println("Found task: " + t.getTitle()));
    }
    
    @Test
    void testQueryTasksByStatusUsingGSI() {
        for (int i = 1; i <= 4; i++) {
            Task task = new Task();
            task.setId(UUID.randomUUID().toString());
            task.setTitle("Task " + i);
            task.setDescription("Status test");
            task.setStatus(i % 2 == 0 ? "completed" : "pending");
            taskTable.putItem(task);
        }

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

        DynamoDbIndex<Task> statusIndex = enhancedClient
            .table("Tasks", TableSchema.fromBean(Task.class))
            .index("status-index");

        PageIterable<Task> iterable = PageIterable.create(statusIndex.query(
            r -> r.queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue("pending").build()))
        ));

        List<Task> pendingTasks = new ArrayList<>();
        iterable.items().forEach(pendingTasks::add);

        assertFalse(pendingTasks.isEmpty());
        pendingTasks.forEach(t -> System.out.println("🟡 Found pending task: " + t.getTitle()));
    }


}
