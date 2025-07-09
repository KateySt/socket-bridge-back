package com.socket.quizzes.repo;

import com.socket.quizzes.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    @Query("SELECT SUM(r.correctAnswers) FROM QuizResult r WHERE r.userId = :userId AND r.companyId = :companyId")
    Integer getTotalCorrectByUserAndCompany(String userId, Long companyId);

    @Query("SELECT SUM(r.totalQuestions) FROM QuizResult r WHERE r.userId = :userId AND r.companyId = :companyId")
    Integer getTotalQuestionsByUserAndCompany(String userId, Long companyId);

    List<QuizResult> findAllByUserId(String userId);

    @Query("SELECT SUM(r.correctAnswers) FROM QuizResult r WHERE r.companyId = :companyId")
    Integer getTotalCorrectByCompany(@Param("companyId") Long companyId);

    @Query("SELECT SUM(r.totalQuestions) FROM QuizResult r WHERE r.companyId = :companyId")
    Integer getTotalQuestionsByCompany(@Param("companyId") Long companyId);

    @Query("""
              SELECT q.id, q.title, MAX(r.completedAt)
              FROM QuizResult r
              JOIN r.quiz q
              WHERE r.companyId = :companyId
              GROUP BY q.id, q.title
            """)
    List<Object[]> getLastCompletionForQuizzesByCompany(@Param("companyId") Long companyId);

    @Query("""
              SELECT q.id, q.title, DATE(r.completedAt), AVG(r.correctAnswers * 1.0 / r.totalQuestions * 10)
              FROM QuizResult r
              JOIN r.quiz q
              GROUP BY q.id, q.title, DATE(r.completedAt)
            """)
    List<Object[]> getAverageScoreByQuizGrouped();

    @Query("""
              SELECT r.userId, DATE(r.completedAt), AVG(r.correctAnswers * 1.0 / r.totalQuestions * 10)
              FROM QuizResult r
              GROUP BY r.userId, DATE(r.completedAt)
            """)
    List<Object[]> getAverageScoreByUserGrouped();

    @Query("""
              SELECT q.id, q.title, DATE(r.completedAt), AVG(r.correctAnswers * 1.0 / r.totalQuestions * 10)
              FROM QuizResult r
              JOIN r.quiz q
              WHERE r.userId = :userId
              GROUP BY q.id, q.title, DATE(r.completedAt)
            """)
    List<Object[]> getUserQuizScoreGrouped(@Param("userId") String userId);

    @Query("""
              SELECT r.userId, MAX(r.completedAt)
              FROM QuizResult r
              WHERE r.companyId = :companyId
              GROUP BY r.userId
            """)
    List<Object[]> getLastTestPerUserByCompany(@Param("companyId") Long companyId);
}
