package com.socket.quizzes.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizAttemptRequest {
    private Long quizId;
    private Long companyId;
    private List<QuizAnswer> answers;
}
