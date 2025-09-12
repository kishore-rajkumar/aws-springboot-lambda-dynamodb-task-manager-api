package com.kishore.taskmanager.repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Repository;

import com.kishore.taskmanager.model.Task;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
public class TaskRepository {

    private final DynamoDbTable<Task> taskTable;

    public TaskRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

        this.taskTable = enhancedClient.table("Tasks", TableSchema.fromBean(Task.class));
    }

    public Task getTask(String id) {
        return taskTable.getItem(Key.builder().partitionValue(id).build());
    }

    public void saveTask(Task task) {
        taskTable.putItem(task);
    }

    public void deleteTask(String id) {
        taskTable.deleteItem(Key.builder().partitionValue(id).build());
    }

    public List<Task> getAllTasks() {
        return StreamSupport.stream(taskTable.scan().items().spliterator(), false)
            .collect(Collectors.toList());
    }
	
}
