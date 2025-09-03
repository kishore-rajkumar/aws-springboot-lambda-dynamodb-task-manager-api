package com.kishore.taskmanager.service;

import com.kishore.taskmanager.model.Task;
import com.kishore.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task create(Task task) {
        task.setId(UUID.randomUUID().toString());
        repository.saveTask(task);
        return task;
    }

    public Task read(String id) {
        return repository.getTask(id);
    }

    public Task update(String id, Task task) {
        task.setId(id);
        repository.saveTask(task);
        return task;
    }

    public void delete(String id) {
        repository.deleteTask(id);
    }
}
