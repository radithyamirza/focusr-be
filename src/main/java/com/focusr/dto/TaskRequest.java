package com.focusr.dto;

import com.focusr.entity.TaskStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Title is required")
        String title,

        TaskStatus status,

        @Min(value = 1, message = "Priority must be between 1 and 3")
        @Max(value = 3, message = "Priority must be between 1 and 3")
        Integer priority,

        Integer estimatedPomodoros,

        Integer completedPomodoros,

        LocalDate dueDate
) {}
