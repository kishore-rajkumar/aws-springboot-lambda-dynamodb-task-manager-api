package com.kishore.taskmanager.service;

import com.kishore.taskmanager.model.Task;
import com.kishore.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

	private final TaskRepository repository;

	public TaskService(TaskRepository repository) {
		this.repository = repository;
	}

	public Task create(Task task) {
		if (task == null)
			throw new IllegalArgumentException("Task cannot be null");

		task.setId(UUID.randomUUID().toString());
		repository.saveTask(task);
		return task;
	}

	public Task read(String id) {
		if (id == null || id.isBlank())
			throw new IllegalArgumentException("ID cannot be null or empty");
		
		Task task = repository.getTask(id);
		
		if(task==null)
			throw new NoSuchElementException("Task not found");
		
		return task;
	}

	public Task update(String id, Task task) {
		task.setId(id);
		repository.saveTask(task);
		return task;
	}

	public void delete(String id) {
		if (id == null || id.isBlank())
			throw new IllegalArgumentException("ID cannot be null or empty");
		
		repository.deleteTask(id);
	}

	public List<Task> findAll(Optional<String> status, Optional<Integer> limit) {
		return repository.getAllTasks(status, limit);
	}

}
