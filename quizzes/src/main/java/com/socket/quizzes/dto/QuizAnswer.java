package com.socket.quizzes.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizAnswer {
    private Long questionId;
    private List<String> selectedOptions;
}
