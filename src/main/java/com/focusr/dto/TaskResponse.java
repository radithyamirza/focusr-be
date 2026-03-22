package com.focusr.dto;

import com.focusr.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        TaskStatus status,
        Integer priority,
        Integer estimatedPomodoros,
        Integer completedPomodoros,
        LocalDate dueDate,
        Instant createdAt
) {}
