package com.socket.quizzes.repo;

import com.socket.quizzes.dto.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    @Query("SELECT SUM(r.correctAnswers) FROM QuizResult r WHERE r.userId = :userId")
    Integer getTotalCorrectByUser(String userId);

    @Query("SELECT SUM(r.totalQuestions) FROM QuizResult r WHERE r.userId = :userId")
    Integer getTotalQuestionsByUser(String userId);
}
