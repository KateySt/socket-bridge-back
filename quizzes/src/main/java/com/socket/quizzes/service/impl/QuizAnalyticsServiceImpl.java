package com.socket.quizzes.service.impl;

import com.socket.quizzes.repo.QuizResultRepository;
import com.socket.quizzes.service.QuizAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizAnalyticsServiceImpl implements QuizAnalyticsService {

    private final QuizResultRepository resultRepository;
    private final RestTemplate restTemplate;

    @Override
    public double getAverageScoreForCompany(Long companyId) {
        Integer correct = resultRepository.getTotalCorrectByCompany(companyId);
        Integer total = resultRepository.getTotalQuestionsByCompany(companyId);

        if (correct == null || total == null || total == 0) {
            return 0;
        }

        return Math.round((correct * 10.0 / total) * 10.0) / 10.0;
    }

    @Override
    public List<Map<String, Object>> getLastCompletionsByQuizForCompany(Long companyId, String userId) throws AccessDeniedException {
        validatePermissions(companyId, userId);

        List<Object[]> raw = resultRepository.getLastCompletionForQuizzesByCompany(companyId);

        return raw.stream().map(row -> Map.of(
                "quizId", row[0],
                "quizTitle", row[1],
                "lastCompletedAt", row[2]
        )).toList();
    }

    @Override
    public List<Map<String, Object>> getAverageScoresByQuizOverTime() {
        List<Object[]> raw = resultRepository.getAverageScoreByQuizGrouped();

        return raw.stream().map(row -> Map.of(
                "quizId", row[0],
                "quizTitle", row[1],
                "date", row[2],
                "avgScore", row[3]
        )).toList();
    }

    @Override
    public List<Map<String, Object>> getAverageScoresByUserOverTime() {
        List<Object[]> raw = resultRepository.getAverageScoreByUserGrouped();

        return raw.stream().map(row -> Map.of(
                "userId", row[0],
                "date", row[1],
                "avgScore", row[2]
        )).toList();
    }

    @Override
    public List<Map<String, Object>> getUserQuizAverageScoreOverTime(String userId) {
        List<Object[]> raw = resultRepository.getUserQuizScoreGrouped(userId);

        return raw.stream().map(row -> Map.of(
                "quizId", row[0],
                "quizTitle", row[1],
                "date", row[2],
                "avgScore", row[3]
        )).toList();
    }

    @Override
    public List<Map<String, Object>> getCompanyUsersLastTests(Long companyId, String userId) throws AccessDeniedException {
        validatePermissions(companyId, userId);

        List<Object[]> raw = resultRepository.getLastTestPerUserByCompany(companyId);
        return raw.stream().map(row -> Map.of(
                "userId", row[0],
                "lastCompletedAt", row[1]
        )).toList();
    }

    private void validatePermissions(Long companyId, String userId) throws AccessDeniedException {
        String url = "http://COMPANY/api/memberships/check?userId=" + userId + "&companyId=" + companyId;
        Boolean hasPermission = restTemplate.getForObject(url, Boolean.class);
        if (!Boolean.TRUE.equals(hasPermission)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
