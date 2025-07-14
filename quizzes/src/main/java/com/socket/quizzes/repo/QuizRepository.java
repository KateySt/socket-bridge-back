package com.socket.quizzes.repo;

import com.socket.quizzes.model.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Page<Quiz> findByCompanyId(Long companyId, Pageable pageable);

    List<Quiz> findByCompanyId(Long companyId);
}