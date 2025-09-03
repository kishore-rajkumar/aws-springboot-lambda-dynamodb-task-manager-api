package com.kishore.taskmanager.controller;

import com.kishore.taskmanager.model.Task;
import com.kishore.taskmanager.service.TaskService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
