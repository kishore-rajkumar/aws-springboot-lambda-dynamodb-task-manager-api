package com.kishore.taskmanager;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.kishore.taskmanager.model.Task;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@SpringBootTest(classes = {TestAwsConfig.class,DynamoDbClient.class})
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class TaskRepositoryIntegrationTest {

    @Container
    static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
        .withServices(DYNAMODB);

    private DynamoDbClient dynamoDbClient;
    private DynamoDbTable<Task> taskTable;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.dynamodb.endpoint", () -> localstack.getEndpointOverride(DYNAMODB).toString());
        registry.add("aws.dynamodb.region", () -> localstack.getRegion().toString());
    }

    @BeforeAll
    void setup() {
    	
    	dynamoDbClient = DynamoDbClient.builder()
    	        .endpointOverride(localstack.getEndpointOverride(DYNAMODB))
    	        .region(Region.of(localstack.getRegion().toString()))
    	        .credentialsProvider(StaticCredentialsProvider.create(
    	            AwsBasicCredentials.create("accesskey", "secretkey")
    	        ))
    	        .build();

        dynamoDbClient.createTable(CreateTableRequest.builder()
            .tableName("Tasks")
            .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
            .attributeDefinitions(AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build())
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


}
