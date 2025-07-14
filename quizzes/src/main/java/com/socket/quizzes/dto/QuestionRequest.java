package com.socket.quizzes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record QuestionRequest(
        @NotBlank(message = "Question text is required")
        String text,

        @NotEmpty(message = "Options must not be empty")
        List<@NotBlank(message = "Option must not be blank") String> options,

        @NotEmpty(message = "At least one correct answer is required")
        List<@NotBlank(message = "Correct answer must not be blank") String> correctAnswers
) {}