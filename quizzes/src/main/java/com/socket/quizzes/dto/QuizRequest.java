package com.socket.quizzes.dto;

import java.util.List;

public record QuizRequest(
        Long companyId,
        String title,
        String description,
        int frequencyDays,
        List<QuestionRequest> questions
) {
}