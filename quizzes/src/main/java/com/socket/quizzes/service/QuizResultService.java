package com.socket.quizzes.service;

import com.socket.quizzes.dto.QuizAttemptRequest;
import com.socket.quizzes.dto.QuizAttemptResponse;
import com.socket.quizzes.model.QuizResult;

import java.util.List;
import java.util.Map;

public interface QuizResultService {
    QuizAttemptResponse processAttempt(QuizAttemptRequest request, String userId);

    double getAverageScoreByUser(String userId, Long companyId);

    List<QuizResult> getExportedResults(String userId, String requesterId);

    String exportToCsv(List<QuizResult> results);

    List<Map<String, Object>> exportToJson(List<QuizResult> results);
}
