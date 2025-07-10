package com.socket.quizzes.mapper;

import com.socket.quizzes.dto.QuestionRequest;
import com.socket.quizzes.dto.QuizAttemptResponse;
import com.socket.quizzes.dto.QuizRequest;
import com.socket.quizzes.model.Question;
import com.socket.quizzes.model.Quiz;
import com.socket.quizzes.model.QuizResult;

import java.util.List;
import java.util.Map;

public interface QuizMapper {

    QuizAttemptResponse toAttemptResponse(int correctAnswers, int totalQuestions);

    Map<String, Object> toJsonMap(QuizResult result, double score);

    String toCsvLine(QuizResult result, double score);

    QuizResult toQuizResult(Quiz quiz, String userId, Long companyId, int correctCount, int totalQuestions);

    List<Question> toQuestions(List<QuestionRequest> questionRequests, Quiz quiz);

    Question toQuestion(QuestionRequest questionRequest, Quiz quiz);

    Quiz toQuiz(QuizRequest request);
}