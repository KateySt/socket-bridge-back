package com.socket.quizzes.controller;

import com.socket.quizzes.enums.ExportFormat;
import com.socket.quizzes.dto.QuizAttemptRequest;
import com.socket.quizzes.dto.QuizAttemptResponse;
import com.socket.quizzes.model.QuizResult;
import com.socket.quizzes.service.QuizResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz-results")
@RequiredArgsConstructor
public class QuizResultController {

    private final QuizResultService quizResultService;

    @PostMapping("/attempt")
    public ResponseEntity<QuizAttemptResponse> submitAttempt(@Valid @RequestBody QuizAttemptRequest request,
                                                             @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(quizResultService.processAttempt(request, userId));
    }

    @GetMapping("/average-score")
    public ResponseEntity<Double> getAverageScore(@RequestHeader("X-User-Id") String userId, @RequestHeader("companyId") Long companyId) {
        return ResponseEntity.ok(quizResultService.getAverageScoreByUser(userId, companyId));
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportResults(
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String userId,
            @RequestHeader("X-User-Id") String requesterId
    ) {
        ExportFormat exportFormat = ExportFormat.from(format);
        List<QuizResult> results = quizResultService.getExportedResults(userId, requesterId);

        if (exportFormat == ExportFormat.CSV) {
            String csv = quizResultService.exportToCsv(results);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=results.csv")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(csv);
        }

        List<Map<String, Object>> data = quizResultService.exportToJson(results);
        return ResponseEntity.ok(data);
    }
}
