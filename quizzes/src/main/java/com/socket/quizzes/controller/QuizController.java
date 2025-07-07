package com.socket.quizzes.controller;

import com.socket.quizzes.dto.Quiz;
import com.socket.quizzes.dto.QuizRequest;
import com.socket.quizzes.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<Quiz> create(@RequestBody QuizRequest request,
                                       @RequestHeader("X-User-Id") String userId) throws AccessDeniedException {
        return ResponseEntity.ok(quizService.createQuiz(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quiz> update(@PathVariable Long id,
                                       @RequestBody QuizRequest request,
                                       @RequestHeader("X-User-Id") String userId) throws AccessDeniedException {
        return ResponseEntity.ok(quizService.updateQuiz(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @RequestHeader("X-User-Id") String userId) throws AccessDeniedException {
        quizService.deleteQuiz(id, userId);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Quiz>> listByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(quizService.findAllByCompany(companyId));
    }
}
