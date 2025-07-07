package com.socket.quizzes.controller;

import com.socket.quizzes.dto.QuizAttemptRequest;
import com.socket.quizzes.dto.QuizAttemptResponse;
import com.socket.quizzes.service.QuizResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz-results")
@RequiredArgsConstructor
public class QuizResultController {

    private final QuizResultService quizResultService;

    @PostMapping("/attempt")
    public ResponseEntity<QuizAttemptResponse> submitAttempt(@RequestBody QuizAttemptRequest request,
                                                             @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(quizResultService.processAttempt(request, userId));
    }

    @GetMapping("/average-score")
    public ResponseEntity<Double> getAverageScore(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(quizResultService.getAverageScoreByUser(userId));
    }
}
