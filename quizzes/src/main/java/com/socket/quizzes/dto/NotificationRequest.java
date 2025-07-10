package com.socket.quizzes.dto;

import lombok.Builder;

@Builder
public record NotificationRequest(
        String userId,
        String text
) {
}
