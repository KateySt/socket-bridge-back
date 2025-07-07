package com.socket.quizzes.service;

import com.socket.quizzes.dto.*;
import com.socket.quizzes.repo.QuizRepository;
import com.socket.quizzes.repo.QuizResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizResultService {

    private final QuizRepository quizRepository;
    private final QuizResultRepository resultRepository;

    public QuizAttemptResponse processAttempt(QuizAttemptRequest request, String userId) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Map<Long, List<String>> correctAnswersMap = quiz.getQuestions().stream()
                .collect(Collectors.toMap(Question::getId, Question::getCorrectAnswer));

        int correctCount = 0;

        for (QuizAnswer answer : request.getAnswers()) {
            List<String> correct = correctAnswersMap.get(answer.getQuestionId());
            List<String> userAns = answer.getSelectedOptions();

            if (correct != null && userAns != null &&
                    new HashSet<>(correct).equals(new HashSet<>(userAns))) {
                correctCount++;
            }
        }

        int totalQuestions = quiz.getQuestions().size();

        QuizResult result = QuizResult.builder()
                .quiz(quiz)
                .userId(userId)
                .companyId(request.getCompanyId())
                .startedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .correctAnswers(correctCount)
                .totalQuestions(totalQuestions)
                .build();

        resultRepository.save(result);

        return QuizAttemptResponse.builder()
                .correctAnswers(correctCount)
                .totalQuestions(totalQuestions)
                .score(Math.round((correctCount * 10.0 / totalQuestions) * 10.0) / 10.0)
                .build();
    }

    public double getAverageScoreByUser(String userId) {
        Integer correct = resultRepository.getTotalCorrectByUser(userId);
        Integer total = resultRepository.getTotalQuestionsByUser(userId);

        if (correct == null || total == null || total == 0) return 0;

        return Math.round((correct * 10.0 / total) * 10.0) / 10.0;
    }
}
