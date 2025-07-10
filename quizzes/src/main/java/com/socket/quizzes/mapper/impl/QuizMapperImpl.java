package com.socket.quizzes.mapper.impl;

import com.socket.quizzes.dto.QuestionRequest;
import com.socket.quizzes.dto.QuizAttemptResponse;
import com.socket.quizzes.dto.QuizRequest;
import com.socket.quizzes.mapper.QuizMapper;
import com.socket.quizzes.model.Question;
import com.socket.quizzes.model.Quiz;
import com.socket.quizzes.model.QuizResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class QuizMapperImpl implements QuizMapper {

    @Override
    public QuizAttemptResponse toAttemptResponse(int correctAnswers, int totalQuestions) {
        double score = Math.round((correctAnswers * 10.0 / totalQuestions) * 10.0) / 10.0;

        return QuizAttemptResponse.builder()
                .correctAnswers(correctAnswers)
                .totalQuestions(totalQuestions)
                .score(score)
                .build();
    }

    @Override
    public Map<String, Object> toJsonMap(QuizResult result, double score) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", result.getUserId());
        map.put("quizId", result.getQuiz().getId());
        map.put("companyId", result.getCompanyId());
        map.put("score", score);
        map.put("submittedAt", result.getCompletedAt());
        return map;
    }

    @Override
    public String toCsvLine(QuizResult result, double score) {
        return result.getUserId() + "," +
                result.getQuiz().getId() + "," +
                result.getCompanyId() + "," +
                score + "," +
                result.getCompletedAt() + "\n";
    }

    @Override
    public QuizResult toQuizResult(Quiz quiz, String userId, Long companyId, int correctCount, int totalQuestions) {
        return QuizResult.builder()
                .quiz(quiz)
                .userId(userId)
                .companyId(companyId)
                .startedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .correctAnswers(correctCount)
                .totalQuestions(totalQuestions)
                .build();
    }

    @Override
    public List<Question> toQuestions(List<QuestionRequest> questionRequests, Quiz quiz) {
        return questionRequests.stream()
                .map(q -> Question.builder()
                        .text(q.text())
                        .options(q.options())
                        .correctAnswer(q.correctAnswers())
                        .quiz(quiz)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Question toQuestion(QuestionRequest questionRequest, Quiz quiz) {
        return Question.builder()
                .text(questionRequest.text())
                .options(questionRequest.options())
                .correctAnswer(questionRequest.correctAnswers())
                .quiz(quiz)
                .build();
    }

    @Override
    public Quiz toQuiz(QuizRequest request) {
        return Quiz.builder()
                .companyId(request.companyId())
                .title(request.title())
                .description(request.description())
                .frequencyDays(request.frequencyDays())
                .build();
    }
}