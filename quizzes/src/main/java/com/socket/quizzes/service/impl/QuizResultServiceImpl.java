package com.socket.quizzes.service.impl;

import com.socket.quizzes.dto.QuizAnswer;
import com.socket.quizzes.dto.QuizAttemptRequest;
import com.socket.quizzes.dto.QuizAttemptResponse;
import com.socket.quizzes.mapper.QuizMapper;
import com.socket.quizzes.model.Question;
import com.socket.quizzes.model.Quiz;
import com.socket.quizzes.model.QuizResult;
import com.socket.quizzes.repo.QuizRepository;
import com.socket.quizzes.repo.QuizResultRepository;
import com.socket.quizzes.service.QuizResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizResultServiceImpl implements QuizResultService {

    private final QuizRepository quizRepository;
    private final QuizResultRepository resultRepository;
    private final RestTemplate restTemplate;
    private final QuizMapper quizMapper;

    @Override
    public QuizAttemptResponse processAttempt(QuizAttemptRequest request, String userId) {
        Quiz quiz = quizRepository.findById(request.quizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Map<Long, List<String>> correctAnswersMap = quiz.getQuestions().stream()
                .collect(Collectors.toMap(Question::getId, Question::getCorrectAnswer));

        int correctCount = 0;

        for (QuizAnswer answer : request.answers()) {
            List<String> correct = correctAnswersMap.get(answer.questionId());
            List<String> userAns = answer.selectedOptions();

            if (correct != null && userAns != null &&
                    new HashSet<>(correct).equals(new HashSet<>(userAns))) {
                correctCount++;
            }
        }

        int totalQuestions = quiz.getQuestions().size();

        QuizResult result = quizMapper.toQuizResult(quiz, userId, request.companyId(), correctCount, totalQuestions);

        resultRepository.save(result);

        return quizMapper.toAttemptResponse(correctCount, totalQuestions);
    }

    @Override
    public double getAverageScoreByUser(String userId, Long companyId) {
        Integer correct = resultRepository.getTotalCorrectByUserAndCompany(userId, companyId);
        Integer total = resultRepository.getTotalQuestionsByUserAndCompany(userId, companyId);
        if (correct == null || total == null || total == 0) return 0;

        return Math.round((correct * 10.0 / total) * 10.0) / 10.0;
    }

    @Override
    public List<QuizResult> getExportedResults(String userId, String requesterId) {
        List<QuizResult> userResults = resultRepository.findAllByUserId(userId);

        return userResults.stream()
                .filter(result -> {
                    try {
                        validatePermissions(result.getCompanyId(), requesterId);
                        return true;
                    } catch (AccessDeniedException e) {
                        return false;
                    }
                })
                .toList();
    }

    @Override
    public String exportToCsv(List<QuizResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("User ID,Quiz ID,Company ID,Score,Submitted At\n");

        for (QuizResult result : results) {
            double score = getAverageScoreByUser(result.getUserId(), result.getCompanyId());
            sb.append(quizMapper.toCsvLine(result, score));
        }

        return sb.toString();
    }

    @Override
    public List<Map<String, Object>> exportToJson(List<QuizResult> results) {
        return results.stream()
                .map(result -> {
                    double score = getAverageScoreByUser(result.getUserId(), result.getCompanyId());
                    return quizMapper.toJsonMap(result, score);
                })
                .toList();
    }

    private void validatePermissions(Long companyId, String userId) throws AccessDeniedException {
        String url = "http://COMPANY/api/memberships/check?userId=" + userId + "&companyId=" + companyId;
        Boolean hasPermission = restTemplate.getForObject(url, Boolean.class);
        if (!Boolean.TRUE.equals(hasPermission)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
