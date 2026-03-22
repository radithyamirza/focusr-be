package com.focusr.service;

import com.focusr.dto.TaskRequest;
import com.focusr.dto.TaskResponse;
import com.focusr.dto.TaskStatusRequest;
import com.focusr.entity.Task;
import com.focusr.entity.TaskStatus;
import com.focusr.entity.User;
import com.focusr.exception.ResourceNotFoundException;
import com.focusr.repository.TaskRepository;
import com.focusr.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<TaskResponse> getTasksForUser(UserDetails userDetails) {
        User user = getUser(userDetails);
        return taskRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TaskResponse createTask(UserDetails userDetails, TaskRequest request) {
        User user = getUser(userDetails);
        Task task = new Task();
        task.setUser(user);
        applyRequest(task, request);
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(UserDetails userDetails, UUID id, TaskRequest request) {
        User user = getUser(userDetails);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
        applyRequest(task, request);
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(UserDetails userDetails, UUID id) {
        User user = getUser(userDetails);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse updateStatus(UserDetails userDetails, UUID id, TaskStatusRequest request) {
        User user = getUser(userDetails);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
        task.setStatus(request.status());
        return toResponse(taskRepository.save(task));
    }

    private void applyRequest(Task task, TaskRequest request) {
        task.setTitle(request.title());
        if (request.status() != null) task.setStatus(request.status());
        if (request.priority() != null) task.setPriority(request.priority());
        if (request.estimatedPomodoros() != null) task.setEstimatedPomodoros(request.estimatedPomodoros());
        if (request.completedPomodoros() != null) task.setCompletedPomodoros(request.completedPomodoros());
        task.setDueDate(request.dueDate());
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getStatus(),
                task.getPriority(),
                task.getEstimatedPomodoros(),
                task.getCompletedPomodoros(),
                task.getDueDate(),
                task.getCreatedAt()
        );
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
