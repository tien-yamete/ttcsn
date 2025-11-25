package com.tien.notificationservice.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tien.event.dto.NotificationEvent;
import com.tien.notificationservice.dto.request.Recipient;
import com.tien.notificationservice.dto.request.SendEmailRequest;
import com.tien.notificationservice.service.EmailService;
import com.tien.notificationservice.service.NotificationService;

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
	NotificationService notificationService;

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
			// Send email
			emailService.sendEmail(SendEmailRequest.builder()
					.to(Recipient.builder().email(message.getRecipient()).build())
					.subject(message.getSubject())
					.htmlContent(message.getBody())
					.build());
			log.info("Email sent successfully to: {}", message.getRecipient());

			// Save notification to database if userId is provided in param map
			if (message.getParam() != null) {
				try {
					Object userIdObj = message.getParam().get("userId");
					if (userIdObj != null) {
						String userId = userIdObj.toString();
						String type = message.getParam().get("type") != null 
								? message.getParam().get("type").toString() 
								: "SYSTEM";
						String relatedUserId = message.getParam().get("relatedUserId") != null 
								? message.getParam().get("relatedUserId").toString() 
								: null;
						String relatedEntityId = message.getParam().get("relatedEntityId") != null 
								? message.getParam().get("relatedEntityId").toString() 
								: null;
						String relatedEntityType = message.getParam().get("relatedEntityType") != null 
								? message.getParam().get("relatedEntityType").toString() 
								: null;

						notificationService.createNotification(
								userId,
								type,
								message.getSubject() != null ? message.getSubject() : "Notification",
								message.getBody() != null ? message.getBody() : "",
								relatedUserId,
								relatedEntityId,
								relatedEntityType);
						log.info("Notification saved to database for user: {}", userId);
					}
				} catch (Exception e) {
					log.error("Failed to save notification to database", e);
				}
			}
		} catch (Exception e) {
			log.error("Failed to send email to: {}", message.getRecipient(), e);
			// Don't rethrow to avoid Kafka message retry loop
		}
	}
}
