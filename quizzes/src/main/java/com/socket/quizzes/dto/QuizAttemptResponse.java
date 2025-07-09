package com.socket.quizzes.dto;

import lombok.Builder;

@Builder
public record QuizAttemptResponse(
        int correctAnswers,
        int totalQuestions,
        double score
) {
}
