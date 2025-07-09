package com.socket.quizzes.dto;

import java.util.List;

public record QuizAttemptRequest(
        Long quizId,
        Long companyId,
        List<QuizAnswer> answers
) {
}
