package com.socket.quizzes.service;

import com.socket.quizzes.dto.QuizRequest;
import com.socket.quizzes.model.Quiz;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface QuizService {

    Quiz createQuiz(QuizRequest request, String userId) throws AccessDeniedException;

    Quiz updateQuiz(Long id, QuizRequest request, String userId) throws AccessDeniedException;

    void deleteQuiz(Long id, String userId) throws AccessDeniedException;

    List<Quiz> findAllByCompany(Long companyId);
}
