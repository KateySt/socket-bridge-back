package com.socket.notification.service;

import com.socket.notification.entity.Notification;
import com.socket.notification.enums.NotificationStatus;
import com.socket.notification.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository repository;

    private final ConcurrentMap<String, Sinks.Many<Notification>> sinks = new ConcurrentHashMap<>();

    public Flux<Notification> streamNotificationsForUser(String userId) {
        Flux<Notification> history = repository.findByUserIdAndStatusNotOrderByCreatedAtDesc(userId, NotificationStatus.READ);
        Flux<Notification> realtime = getSink(userId)
                .asFlux()
                .filter(notification -> notification.getStatus() != NotificationStatus.READ);
        return history.concatWith(realtime);
    }

    public void saveNotification(String userId, String text) {
        Notification notification = Notification.builder()
                .userId(userId)
                .status(NotificationStatus.UNREAD)
                .text(text)
                .createdAt(Instant.now())
                .build();
        getSink(userId).tryEmitNext(notification);
        repository.save(notification).subscribe();
    }

    public Mono<Void> markAllAsRead(String userId) {
        return repository.findByUserId(userId)
                .flatMap(notification -> {
                    notification.setStatus(NotificationStatus.READ);
                    return repository.save(notification);
                })
                .then();
    }

    private Sinks.Many<Notification> getSink(String userId) {
        return sinks.computeIfAbsent(userId, k -> Sinks.many().multicast().onBackpressureBuffer());
    }
}
