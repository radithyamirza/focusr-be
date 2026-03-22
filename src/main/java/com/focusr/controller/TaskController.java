package com.focusr.controller;

import com.focusr.dto.TaskRequest;
import com.focusr.dto.TaskResponse;
import com.focusr.dto.TaskStatusRequest;
import com.focusr.security.CurrentUser;
import com.focusr.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@CurrentUser UserDetails currentUser) {
        return ResponseEntity.ok(taskService.getTasksForUser(currentUser));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@CurrentUser UserDetails currentUser,
                                                   @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(currentUser, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@CurrentUser UserDetails currentUser,
                                                   @PathVariable UUID id,
                                                   @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(currentUser, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@CurrentUser UserDetails currentUser,
                                           @PathVariable UUID id) {
        taskService.deleteTask(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@CurrentUser UserDetails currentUser,
                                                     @PathVariable UUID id,
                                                     @Valid @RequestBody TaskStatusRequest request) {
        return ResponseEntity.ok(taskService.updateStatus(currentUser, id, request));
    }
}
