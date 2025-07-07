package com.socket.quizzes.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {
    private String text;
    private List<String> options;
    private List<String> correctAnswers;
}

