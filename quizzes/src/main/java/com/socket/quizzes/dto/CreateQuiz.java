package com.socket.quizzes.dto;

import java.util.List;

public record CreateQuiz(
        Long companyId,
        String title,
        String description,
        int frequencyDays,
        String ownerId,
        List<CreateQuestion> questions
) {}
