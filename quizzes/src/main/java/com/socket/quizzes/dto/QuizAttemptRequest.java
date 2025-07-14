package com.socket.quizzes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuizAttemptRequest(

        @NotNull(message = "Quiz ID is required")
        Long quizId,

        @NotNull(message = "Company ID is required")
        Long companyId,

        @NotEmpty(message = "At least one answer is required")
        List<@Valid QuizAnswer> answers

) {}
