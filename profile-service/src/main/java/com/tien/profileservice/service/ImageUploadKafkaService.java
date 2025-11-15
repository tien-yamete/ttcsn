package com.tien.profileservice.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.tien.sharedcommon.converter.MediaConverter;
import com.tien.sharedcontacts.media.ImageTopics;
import com.tien.sharedcontacts.media.ImageUploadEvent;
import com.tien.sharedcontacts.media.ImageUploadedEvent;
import com.tien.sharedcontacts.media.entity.ImageType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageUploadKafkaService {

    KafkaTemplate<String, Object> kafkaTemplate;

    // Map to store pending upload requests
    Map<String, CompletableFuture<ImageUploadedEvent>> pendingUploads = new ConcurrentHashMap<>();

    public ImageUploadedEvent uploadAvatar(MultipartFile file, String ownerId) throws Exception {
        // Convert file to base64
        String base64 = MediaConverter.convertToBase64(List.of(file)).get(0);

        // Create a unique correlation ID
        String correlationId = UUID.randomUUID().toString();

        // Create upload event (correlation ID will be used as Kafka message key)
        ImageUploadEvent event = new ImageUploadEvent(
                List.of(base64),
                ImageType.AVATAR,
                ownerId,
                null,
                null
        );

        // Create future for async response
        CompletableFuture<ImageUploadedEvent> future = new CompletableFuture<>();
        pendingUploads.put(correlationId, future);

        try {
            // Send event to Kafka
            kafkaTemplate.send(ImageTopics.IMAGE_UPLOAD, correlationId, event);
            log.info("Sent image upload event with correlationId: {}", correlationId);

            // Wait for response (with timeout)
            ImageUploadedEvent result = future.get(30, TimeUnit.SECONDS);
            log.info("Received image upload response for correlationId: {}", correlationId);
            return result;
        } catch (Exception e) {
            pendingUploads.remove(correlationId);
            log.error("Failed to upload image via Kafka: {}", e.getMessage(), e);
            throw e;
        } finally {
            pendingUploads.remove(correlationId);
        }
    }

    @KafkaListener(topics = ImageTopics.IMAGE_UPLOADED, groupId = "profile-service-group")
    public void handleImageUploaded(
            @Header(KafkaHeaders.RECEIVED_KEY) String correlationId,
            @Payload ImageUploadedEvent event) {
        log.info("Received image uploaded event with correlationId: {}, event: {}", correlationId, event);
        if (correlationId != null) {
            CompletableFuture<ImageUploadedEvent> future = pendingUploads.get(correlationId);
            if (future != null && !future.isDone()) {
                future.complete(event);
                log.info("Completed future for correlationId: {}", correlationId);
            } else {
                log.warn("No pending future found for correlationId: {}", correlationId);
            }
        } else {
            log.warn("Received image uploaded event without correlationId in message key");
        }
    }
}

