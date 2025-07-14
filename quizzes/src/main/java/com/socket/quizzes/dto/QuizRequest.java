package com.socket.quizzes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuizRequest(
        @NotNull(message = "Company ID is required")
        Long companyId,

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @Min(value = 1, message = "Frequency must be at least 1 day")
        int frequencyDays,

        @NotEmpty(message = "At least one question is required")
        List<@Valid QuestionRequest> questions
) {}
