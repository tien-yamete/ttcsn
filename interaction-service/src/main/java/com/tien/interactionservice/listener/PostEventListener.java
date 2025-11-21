package com.tien.interactionservice.listener;

import com.tien.interactionservice.event.PostEvent;
import com.tien.interactionservice.repository.CommentRepository;
import com.tien.interactionservice.repository.LikeRepository;
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
public class PostEventListener {
    CommentRepository commentRepository;
    LikeRepository likeRepository;

    @KafkaListener(topics = "post.events", groupId = "interaction-service-group")
    public void handlePostEvent(PostEvent event) {
        log.info("Received post event: {}", event);

        if (event == null || event.getPostId() == null) {
            log.error("Invalid post event received");
            return;
        }

        try {
            if ("DELETED".equals(event.getEventType())) {
                // Delete all comments and likes for this post
                commentRepository.deleteByPostId(event.getPostId());
                likeRepository.deleteByPostId(event.getPostId());
                log.info("Deleted all comments and likes for postId: {}", event.getPostId());
            }
        } catch (Exception e) {
            log.error("Error handling post event: {}", e.getMessage(), e);
        }
    }
}

