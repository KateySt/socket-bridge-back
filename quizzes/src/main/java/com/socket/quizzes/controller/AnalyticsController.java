package com.socket.quizzes.controller;

import com.socket.quizzes.service.QuizAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final QuizAnalyticsService analyticsService;

    @GetMapping("/rating")
    public ResponseEntity<Double> getGlobalRating(@RequestParam Long companyId) {
        return ResponseEntity.ok(analyticsService.getAverageScoreForCompany(companyId));
    }

    @GetMapping("/last-completions")
    public ResponseEntity<?> getLastCompletions(@RequestParam Long companyId,
                                                @RequestHeader("X-User-Id") String userId) throws AccessDeniedException {
        return ResponseEntity.ok(analyticsService.getLastCompletionsByQuizForCompany(companyId, userId));
    }

    @GetMapping("/avg-by-quiz")
    public ResponseEntity<?> getAvgByQuiz() {
        return ResponseEntity.ok(analyticsService.getAverageScoresByQuizOverTime());
    }

    @GetMapping("/avg-by-user")
    public ResponseEntity<?> getAvgByUser() {
        return ResponseEntity.ok(analyticsService.getAverageScoresByUserOverTime());
    }

    @GetMapping("/avg-by-user/{userId}")
    public ResponseEntity<?> getAvgByUserAndQuiz(@PathVariable String userId) {
        return ResponseEntity.ok(analyticsService.getUserQuizAverageScoreOverTime(userId));
    }

    @GetMapping("/company-users-last-tests")
    public ResponseEntity<?> getCompanyUsersLastTests(@RequestParam Long companyId,
                                                      @RequestHeader("X-User-Id") String userId) throws AccessDeniedException {
        return ResponseEntity.ok(analyticsService.getCompanyUsersLastTests(companyId, userId));
    }
}
