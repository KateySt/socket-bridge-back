package com.socket.quizzes.dto;

import java.util.List;

public record QuestionRequest(
        String text,
        List<String> options,
        List<String> correctAnswers
) {
}