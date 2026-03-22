package com.focusr.repository;

import com.focusr.entity.PomodoroSession;
import com.focusr.entity.SessionType;
import com.focusr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PomodoroSessionRepository extends JpaRepository<PomodoroSession, UUID> {

    List<PomodoroSession> findByUserAndStartedAtBetweenOrderByStartedAtDesc(
            User user, Instant from, Instant to);

    List<PomodoroSession> findByUserOrderByStartedAtDesc(User user);

    @Query("SELECT COUNT(s) FROM PomodoroSession s WHERE s.user = :user AND s.type = :type AND s.startedAt >= :from AND s.startedAt < :to")
    long countByUserAndTypeAndStartedAtBetween(
            @Param("user") User user,
            @Param("type") SessionType type,
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query("SELECT SUM(s.durationMinutes) FROM PomodoroSession s WHERE s.user = :user AND s.type = :type")
    Long sumDurationByUserAndType(@Param("user") User user, @Param("type") SessionType type);

    @Query("""
            SELECT cast(s.startedAt as LocalDate), COUNT(s)
            FROM PomodoroSession s
            WHERE s.user = :user
              AND s.type = :type
              AND s.startedAt >= :from
            GROUP BY cast(s.startedAt as LocalDate)
            ORDER BY cast(s.startedAt as LocalDate)
            """)
    List<Object[]> countDailyWorkSessionsSince(
            @Param("user") User user,
            @Param("type") SessionType type,
            @Param("from") Instant from);
}
