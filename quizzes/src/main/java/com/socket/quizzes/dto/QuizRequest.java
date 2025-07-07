package com.socket.quizzes.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizRequest {
    private Long companyId;
    private String title;
    private String description;
    private int frequencyDays;
    private List<QuestionRequest> questions;
}
