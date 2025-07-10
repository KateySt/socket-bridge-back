package com.socket.quizzes.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

public interface QuizAnalyticsService {
    double getAverageScoreForCompany(Long companyId);

    List<Map<String, Object>> getLastCompletionsByQuizForCompany(Long companyId, String userId) throws AccessDeniedException;

    List<Map<String, Object>> getAverageScoresByQuizOverTime();

    List<Map<String, Object>> getAverageScoresByUserOverTime();

    List<Map<String, Object>> getUserQuizAverageScoreOverTime(String userId);

    List<Map<String, Object>> getCompanyUsersLastTests(Long companyId, String userId) throws AccessDeniedException;
}
