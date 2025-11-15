package com.tien.fileservice.listener;

import com.tien.sharedcontacts.media.ImageTopics;
import com.tien.sharedcontacts.media.ImageUploadEvent;
import com.tien.sharedcontacts.media.ImageUploadedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.tien.fileservice.service.ImageService;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageUploadListener {

    ImageService imageService;
    KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = ImageTopics.IMAGE_UPLOAD, groupId = "file-service-group")
    public void handleImageUpload(
            @Header(KafkaHeaders.RECEIVED_KEY) String correlationId,
            @Payload ImageUploadEvent event) {
        log.info("Received image upload event with correlationId: {}, event: {}", correlationId, event);
        try {
            ImageUploadedEvent uploadedEvent = imageService.uploadImage(event);
            log.info("Image uploaded successfully, sending response with correlationId: {}", correlationId);
            // Send response with correlation ID as key for proper routing
            kafkaTemplate.send(ImageTopics.IMAGE_UPLOADED, correlationId, uploadedEvent);
        } catch (Exception e) {
            log.error("Failed to upload image with correlationId {}: {}", correlationId, e.getMessage(), e);
            // In production, you might want to send an error event back
        }
    }
}