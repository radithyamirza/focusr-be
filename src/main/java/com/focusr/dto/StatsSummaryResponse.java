package com.focusr.dto;

public record StatsSummaryResponse(
        long todayPomodoros,
        long weekPomodoros,
        long currentStreak,
        long totalFocusMinutes
) {}
