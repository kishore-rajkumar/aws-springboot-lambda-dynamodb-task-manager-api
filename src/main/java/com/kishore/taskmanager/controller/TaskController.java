package com.kishore.taskmanager.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kishore.taskmanager.model.Task;
import com.kishore.taskmanager.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task) {
        Task created = service.create(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
	/*
	 * @GetMapping public ResponseEntity<List<Task>> getAllTasks() { List<Task>
	 * tasks = service.finaAll(); return ResponseEntity.ok(tasks); }
	 */
    
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam Optional<String> status,
            @RequestParam Optional<Integer> limit) {
        List<Task> tasks = service.findAll(status, limit);
        return ResponseEntity.ok(tasks);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Task> read(@PathVariable String id) {
        Task found = service.read(id);
        return ResponseEntity.ok(found);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable String id, @RequestBody Task task) {
        Task updated = service.update(id, task);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
