package com.tien.interactionservice.listener;

import com.tien.interactionservice.event.UserEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserEventListener {

    @KafkaListener(topics = "user.events", groupId = "interaction-service-group")
    public void handleUserEvent(UserEvent event) {
        log.info("Received user event: {}", event);

        if (event == null || event.getUserId() == null) {
            log.error("Invalid user event received");
            return;
        }

        try {
            if ("CREATED".equals(event.getEventType())) {
                // User created - no action needed, just validate user exists
                log.info("User created event received for userId: {}", event.getUserId());
            }
        } catch (Exception e) {
            log.error("Error handling user event: {}", e.getMessage(), e);
        }
    }
}

