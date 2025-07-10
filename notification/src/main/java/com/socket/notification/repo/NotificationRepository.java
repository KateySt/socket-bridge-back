package com.socket.notification.repo;

import com.socket.notification.entity.Notification;
import com.socket.notification.enums.NotificationStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
    Flux<Notification> findByUserIdAndStatusNotOrderByCreatedAtDesc(String userId, NotificationStatus status);

    Flux<Notification> findByUserId(String userId);
}