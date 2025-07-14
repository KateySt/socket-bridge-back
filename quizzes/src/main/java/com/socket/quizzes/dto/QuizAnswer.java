package com.socket.quizzes.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuizAnswer(

        @NotNull(message = "Question ID is required")
        Long questionId,

        @NotEmpty(message = "At least one selected option is required")
        List<@NotNull(message = "Selected option must not be null") String> selectedOptions

) {}