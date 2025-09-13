package com.kishore.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kishore.taskmanager.model.Task;
import com.kishore.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task sampleTask;

    @BeforeEach
    void setup() {
        sampleTask = new Task();
        sampleTask.setId("123");
        sampleTask.setTitle("Mock Task");
        sampleTask.setDescription("Controller test");
        sampleTask.setStatus("PENDING");
    }
    
    
    @Test
    void shouldCreateTaskSuccessfully() throws Exception {

    	when(taskService.create(any(Task.class))).thenReturn(sampleTask); // if it returns Task

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleTask)))
            .andExpect(status().isCreated());

        verify(taskService).create(any(Task.class));
    }
    
    @Test
    void shouldReturnBadRequestForEmptyBody() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldReturnAllTasks() throws Exception {
        when(taskService.findAll(Optional.empty(), Optional.empty()))
            .thenReturn(List.of(sampleTask));

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Mock Task"));
    }
    
    @Test
    void shouldReturnEmptyListWhenNoTasksExist() throws Exception {
        when(taskService.findAll(Optional.empty(), Optional.empty()))
            .thenReturn(List.of());

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
    
    @Test
    void shouldReturnTaskById() throws Exception {
        when(taskService.read("123")).thenReturn(sampleTask);

        mockMvc.perform(get("/tasks/123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("123"))
            .andExpect(jsonPath("$.title").value("Mock Task"));
    }

    @Test
    void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        when(taskService.read("999"))
            .thenThrow(new NoSuchElementException("Task not found"));

        mockMvc.perform(get("/tasks/999"))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        doNothing().when(taskService).delete("123");

        mockMvc.perform(delete("/tasks/123"))
            .andExpect(status().isNoContent());

        verify(taskService).delete("123");
    }

    @Test
    void shouldReturnBadRequestWhenDeleteForBlankId() throws Exception {
        mockMvc.perform(delete("/tasks/ "))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {
        Task updatedTask = new Task();
        updatedTask.setId("123");
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus("COMPLETED");

        when(taskService.update(eq("123"), any(Task.class)))
            .thenReturn(updatedTask);

        mockMvc.perform(put("/tasks/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("123"))
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
    
    @Test
    void shouldReturnBadRequestForBlankIdOnUpdate() throws Exception {
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated");

        mockMvc.perform(put("/tasks/ ")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonexistentTask() throws Exception {
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated");

        when(taskService.update(eq("999"), any(Task.class)))
            .thenThrow(new NoSuchElementException("Task not found"));

        mockMvc.perform(put("/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Task not found"));
    }



}