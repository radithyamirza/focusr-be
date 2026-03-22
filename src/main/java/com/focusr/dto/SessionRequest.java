package com.focusr.dto;

import com.focusr.entity.SessionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record SessionRequest(
        UUID taskId,

        @NotNull(message = "Session type is required")
        SessionType type,

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least 1 minute")
        Integer durationMinutes,

        @NotNull(message = "Start time is required")
        Instant startedAt,

        Instant endedAt
) {}
