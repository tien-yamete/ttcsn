package com.tien.postservice.service;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ImageUploadKafkaService {
    KafkaTemplate<String, Object> kafkaTemplate;

    Map<String, CompletableFuture<ImageUploadedEvent>> pendingUploads = new ConcurrentHashMap<>();

    public List<String> uploadPostImages(List<MultipartFile> files, String ownerId, String postId)
    throws Exception {
        if(files == null || files.isEmpty()) {
            return List.of();
        }
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if(file != null && !file.isEmpty()) {
                ImageUploadedEvent result = uploadSingleImage(file, ownerId, postId);
                if(result != null && result.imageUrl() != null) {
                    imageUrls.add(result.imageUrl());
                }
            }
        }
        return imageUrls;
    }

    private ImageUploadedEvent uploadSingleImage(MultipartFile file, String ownerId, String postId) throws Exception {
        String base64 = MediaConverter.convertToBase64(List.of(file)).get(0);

        String correlationId = UUID.randomUUID().toString();

        ImageUploadEvent event = new ImageUploadEvent(
                List.of(base64),
                ImageType.POST_IMAGE,
                ownerId,
                postId,
                null
        );

        CompletableFuture<ImageUploadedEvent> future = new  CompletableFuture<>();
        pendingUploads.put(correlationId, future);

        try{
            kafkaTemplate.send(ImageTopics.IMAGE_UPLOAD, correlationId, event);

            ImageUploadedEvent result = future.get(30, TimeUnit.SECONDS);

            return result;
        }
        catch (Exception e) {
            pendingUploads.remove(correlationId);
            log.error("Failed to upload post image via Kafka: {}", e.getMessage(), e);
            throw e;
        }
        finally {
            pendingUploads.remove(correlationId);
        }
    }

    @KafkaListener(topics = ImageTopics.IMAGE_UPLOADED, groupId = "post-service-group")
    public void handleImageUploaded(
            @Header (KafkaHeaders.RECEIVED_KEY) String correlationId,
            @Payload ImageUploadedEvent event){
        log.info("Received image uploaded event with correlationId: {}, event: {}", correlationId, event);
        if(correlationId != null){
            CompletableFuture<ImageUploadedEvent> future = pendingUploads.get(correlationId);
            if(future != null && !future.isDone()){
                future.complete(event);
                log.info("Completed future for correlationId: {}", correlationId);
            }
            else {
                log.warn("No pending future found for correlationId: {}", correlationId);
            }
        }
        else {
            log.warn("Received image uploaded event without correlationId in message key");
        }
    }
}
