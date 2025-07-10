package com.socket.notification.controller;

import com.socket.notification.dto.NotificationRequest;
import com.socket.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public Mono<Void> create(@RequestBody NotificationRequest request) {
        System.out.println(request.toString());
        service.saveNotification(request.getUserId(), request.getText());
        return Mono.empty();
    }
}