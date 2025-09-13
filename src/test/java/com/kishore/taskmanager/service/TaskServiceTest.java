package com.kishore.taskmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kishore.taskmanager.model.Task;
import com.kishore.taskmanager.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

	@Mock
	private TaskRepository taskRepository;

	@InjectMocks
	private TaskService taskService;

	private Task sampleTask;

	@BeforeEach
	void setup() {
		sampleTask = new Task();
		sampleTask.setId("123");
		sampleTask.setTitle("Unit Test");
		sampleTask.setDescription("Testing TaskService");
		sampleTask.setStatus("PENDING");
	}

	@Test // Get All Tasks
	void shouldReturnAllTasks() {
		when(taskRepository.getAllTasks(Optional.empty(), Optional.empty())).thenReturn(List.of(sampleTask));

		List<Task> result = taskService.findAll(Optional.empty(), Optional.empty());

		assertEquals(1, result.size());
		assertEquals("Unit Test", result.get(0).getTitle());
	}
	
	@Test // Empty Result from Repository
	void shouldReturnEmptyListWhenNoTasksFound() {
	    when(taskRepository.getAllTasks(Optional.empty(), Optional.empty()))
	        .thenReturn(List.of());

	    List<Task> result = taskService.findAll(Optional.empty(), Optional.empty());

	    assertTrue(result.isEmpty());
	}


	@Test // Get task by Status filter
	void shouldFilterTasksByStatus() {
		when(taskRepository.getAllTasks(Optional.of("PENDING"), Optional.empty())).thenReturn(List.of(sampleTask));

		List<Task> result = taskService.findAll(Optional.of("PENDING"), Optional.empty());

		assertFalse(result.isEmpty());
		assertEquals("PENDING", result.get(0).getStatus());
	}

	@Test // Get Task by ID
	void shouldReturnTaskById() {
		when(taskRepository.getTask("123")).thenReturn(sampleTask);

		Task result = taskService.read("123");

		assertNotNull(result);
		assertEquals("123", result.getId());
	}
	
	@Test  // Null or Empty ID on Get
	void shouldThrowOnNullIdForGet() {
	    assertThrows(IllegalArgumentException.class, () -> taskService.read(null));
	}
	
	@Test  // No task found by ID on Get
	void shouldThrowOnNoTaskFoundGet() {
		when(taskRepository.getTask("9999")).thenReturn(null);
	    assertThrows(NoSuchElementException.class, () -> taskService.read("9999"));
	}

	@Test // Create Task
	void shouldCreateTask() {
		doNothing().when(taskRepository).saveTask(sampleTask);

		taskService.create(sampleTask);

		verify(taskRepository, times(1)).saveTask(sampleTask);
	}

	@Test // Null Task on Create
	void shouldNotCreateNullTask() {
		assertThrows(IllegalArgumentException.class, () -> taskService.create(null));
	}

	@Test // delete task
	void shouldDeleteTask() {
		doNothing().when(taskRepository).deleteTask("123");

		taskService.delete("123");

		verify(taskRepository).deleteTask("123");
	}

	@Test  // Null or Empty ID on Delete
	void shouldThrowOnEmptyIdForDelete() {
	    assertThrows(IllegalArgumentException.class, () -> taskService.delete(""));
	}

}