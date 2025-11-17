package com.tien.identityservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tien.event.dto.NotificationEvent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * NotificationService: Service gửi thông báo qua Kafka.
 * - Notification service khác sẽ consume event này để gửi email thực tế
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    KafkaTemplate<String, Object> kafkaTemplate;

    private static final String NOTIFICATION_TOPIC = "notification-delivery";

    // Gửi event thông báo vào Kafka topic "notification-delivery"
    public void sendNotification(NotificationEvent event) {
        kafkaTemplate.send(NOTIFICATION_TOPIC, event);
    }

    // Tạo và gửi email notification event
    public void sendEmail(String recipient, String subject, String body) {
        NotificationEvent event = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(recipient)
                .subject(subject)
                .body(body)
                .build();
        sendNotification(event);
    }
}
