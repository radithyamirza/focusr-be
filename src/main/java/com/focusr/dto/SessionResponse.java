package com.focusr.dto;

import com.focusr.entity.SessionType;

import java.time.Instant;
import java.util.UUID;

public record SessionResponse(
        UUID id,
        UUID taskId,
        SessionType type,
        Integer durationMinutes,
        Instant startedAt,
        Instant endedAt
) {}
