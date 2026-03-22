package com.focusr.controller;

import com.focusr.dto.SessionRequest;
import com.focusr.dto.SessionResponse;
import com.focusr.security.CurrentUser;
import com.focusr.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> logSession(@CurrentUser UserDetails currentUser,
                                                      @Valid @RequestBody SessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.logSession(currentUser, request));
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> getSessions(
            @CurrentUser UserDetails currentUser,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {
        return ResponseEntity.ok(sessionService.getSessions(currentUser, from, to));
    }
}
