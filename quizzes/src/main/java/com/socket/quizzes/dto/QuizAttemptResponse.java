package com.socket.quizzes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizAttemptResponse {
    private int correctAnswers;
    private int totalQuestions;
    private double score;
}
