package com.focusr.controller;

import com.focusr.dto.DailyStatsResponse;
import com.focusr.dto.StatsSummaryResponse;
import com.focusr.security.CurrentUser;
import com.focusr.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<StatsSummaryResponse> getSummary(@CurrentUser UserDetails currentUser) {
        return ResponseEntity.ok(statsService.getSummary(currentUser));
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyStatsResponse>> getDaily(@CurrentUser UserDetails currentUser) {
        return ResponseEntity.ok(statsService.getDaily(currentUser));
    }
}
