package com.socket.quizzes.dto;

import java.util.List;

public record QuizAnswer(
        Long questionId,
        List<String> selectedOptions
) {
}