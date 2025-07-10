package com.socket.notification.controller;

import com.socket.notification.dto.NotificationRequest;
import com.socket.notification.entity.Notification;
import com.socket.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RSocketController {
    private final NotificationService service;

    @MessageMapping("notifications.channel")
    public Flux<Notification> streamNotificationsForUser(NotificationRequest request) {
        return service.streamNotificationsForUser(request.getUserId())
                .doOnError(e -> log.error("Stream error for {}: {}", request.getUserId(), e.getMessage()));
    }

    @MessageMapping("notifications.markAllAsRead")
    public Mono<Void> markAllNotificationsAsRead(NotificationRequest request) {
        String userId = request.getUserId();
        return service.markAllAsRead(userId)
                .doOnError(e -> log.error("Error marking notifications as read for userId: {}, error: {}", userId, e.getMessage()));
    }
}
