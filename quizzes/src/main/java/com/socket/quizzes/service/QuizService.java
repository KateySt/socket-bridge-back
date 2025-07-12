package com.socket.quizzes.service;

import com.socket.quizzes.dto.QuizRequest;
import com.socket.quizzes.model.Quiz;
import org.springframework.data.domain.Page;

import java.nio.file.AccessDeniedException;

public interface QuizService {

    Quiz createQuiz(QuizRequest request, String userId) throws AccessDeniedException;

    Quiz updateQuiz(Long id, QuizRequest request, String userId) throws AccessDeniedException;

    void deleteQuiz(Long id, String userId) throws AccessDeniedException;

    Page<Quiz> findAllByCompany(Long companyId, int page, int size);
}
