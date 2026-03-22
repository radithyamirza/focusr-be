package com.focusr.dto;

import java.time.LocalDate;

public record DailyStatsResponse(
        LocalDate date,
        long pomodoroCount
) {}
