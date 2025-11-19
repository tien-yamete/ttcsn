package com.tien.notificationservice.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tien.event.dto.NotificationEvent;
import com.tien.notificationservice.dto.request.Recipient;
import com.tien.notificationservice.dto.request.SendEmailRequest;
import com.tien.notificationservice.service.EmailService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    EmailService emailService;

    @KafkaListener(topics = "notification-delivery")
    public void listenNotificationDelivery(NotificationEvent message) {
        log.info("Message received: {}", message);
        
        if (message == null) {
            log.error("Received null NotificationEvent");
            return;
        }
        
        if (message.getRecipient() == null || message.getRecipient().isEmpty()) {
            log.error("NotificationEvent has null or empty recipient");
            return;
        }
        
        if (message.getSubject() == null || message.getSubject().isEmpty()) {
            log.warn("NotificationEvent has null or empty subject for recipient: {}", message.getRecipient());
        }
        
        if (message.getBody() == null || message.getBody().isEmpty()) {
            log.warn("NotificationEvent has null or empty body for recipient: {}", message.getRecipient());
        }
        
        try {
            emailService.sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder().email(message.getRecipient()).build())
                    .subject(message.getSubject())
                    .htmlContent(message.getBody())
                    .build());
            log.info("Email sent successfully to: {}", message.getRecipient());
        } catch (Exception e) {
            log.error("Failed to send email to: {}", message.getRecipient(), e);
            // Don't rethrow to avoid Kafka message retry loop
        }
    }
}
