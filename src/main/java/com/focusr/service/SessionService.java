package com.focusr.service;

import com.focusr.dto.SessionRequest;
import com.focusr.dto.SessionResponse;
import com.focusr.entity.PomodoroSession;
import com.focusr.entity.Task;
import com.focusr.entity.User;
import com.focusr.exception.ResourceNotFoundException;
import com.focusr.repository.PomodoroSessionRepository;
import com.focusr.repository.TaskRepository;
import com.focusr.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SessionService {

    private final PomodoroSessionRepository sessionRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public SessionService(PomodoroSessionRepository sessionRepository,
                          TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SessionResponse logSession(UserDetails userDetails, SessionRequest request) {
        User user = getUser(userDetails);

        PomodoroSession session = new PomodoroSession();
        session.setUser(user);
        session.setType(request.type());
        session.setDurationMinutes(request.durationMinutes());
        session.setStartedAt(request.startedAt());
        session.setEndedAt(request.endedAt());

        if (request.taskId() != null) {
            Task task = taskRepository.findByIdAndUser(request.taskId(), user)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + request.taskId()));
            session.setTask(task);
        }

        return toResponse(sessionRepository.save(session));
    }

    public List<SessionResponse> getSessions(UserDetails userDetails, Instant from, Instant to) {
        User user = getUser(userDetails);
        List<PomodoroSession> sessions;
        if (from != null && to != null) {
            sessions = sessionRepository.findByUserAndStartedAtBetweenOrderByStartedAtDesc(user, from, to);
        } else {
            sessions = sessionRepository.findByUserOrderByStartedAtDesc(user);
        }
        return sessions.stream().map(this::toResponse).toList();
    }

    private SessionResponse toResponse(PomodoroSession session) {
        return new SessionResponse(
                session.getId(),
                session.getTask() != null ? session.getTask().getId() : null,
                session.getType(),
                session.getDurationMinutes(),
                session.getStartedAt(),
                session.getEndedAt()
        );
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
