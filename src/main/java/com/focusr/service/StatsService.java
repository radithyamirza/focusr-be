package com.focusr.service;

import com.focusr.dto.DailyStatsResponse;
import com.focusr.dto.StatsSummaryResponse;
import com.focusr.entity.SessionType;
import com.focusr.entity.User;
import com.focusr.exception.ResourceNotFoundException;
import com.focusr.repository.PomodoroSessionRepository;
import com.focusr.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final PomodoroSessionRepository sessionRepository;
    private final UserRepository userRepository;

    public StatsService(PomodoroSessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public StatsSummaryResponse getSummary(UserDetails userDetails) {
        User user = getUser(userDetails);

        Instant todayStart = LocalDate.now(ZoneOffset.UTC).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant todayEnd = todayStart.plus(1, java.time.temporal.ChronoUnit.DAYS);
        Instant weekStart = LocalDate.now(ZoneOffset.UTC)
                .with(java.time.DayOfWeek.MONDAY)
                .atStartOfDay(ZoneOffset.UTC).toInstant();

        long todayPomodoros = sessionRepository.countByUserAndTypeAndStartedAtBetween(
                user, SessionType.WORK, todayStart, todayEnd);
        long weekPomodoros = sessionRepository.countByUserAndTypeAndStartedAtBetween(
                user, SessionType.WORK, weekStart, Instant.now());
        Long focusMinutesLong = sessionRepository.sumDurationByUserAndType(user, SessionType.WORK);
        long totalFocusMinutes = focusMinutesLong != null ? focusMinutesLong : 0L;

        long currentStreak = computeStreak(user);

        return new StatsSummaryResponse(todayPomodoros, weekPomodoros, currentStreak, totalFocusMinutes);
    }

    public List<DailyStatsResponse> getDaily(UserDetails userDetails) {
        User user = getUser(userDetails);

        Instant thirtyDaysAgo = LocalDate.now(ZoneOffset.UTC).minusDays(29)
                .atStartOfDay(ZoneOffset.UTC).toInstant();

        List<Object[]> rows = sessionRepository.countDailyWorkSessionsSince(user, SessionType.WORK, thirtyDaysAgo);

        Map<LocalDate, Long> countsByDate = rows.stream()
                .collect(Collectors.toMap(
                        row -> (LocalDate) row[0],
                        row -> (Long) row[1]
                ));

        List<DailyStatsResponse> result = new ArrayList<>();
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            result.add(new DailyStatsResponse(date, countsByDate.getOrDefault(date, 0L)));
        }
        return result;
    }

    private long computeStreak(User user) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        long streak = 0;
        LocalDate current = today;
        final int maxDays = 365;

        while (streak < maxDays) {
            Instant dayStart = current.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant dayEnd = dayStart.plus(1, java.time.temporal.ChronoUnit.DAYS);
            long count = sessionRepository.countByUserAndTypeAndStartedAtBetween(
                    user, SessionType.WORK, dayStart, dayEnd);
            if (count == 0) break;
            streak++;
            current = current.minusDays(1);
        }
        return streak;
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
