package com.tien.interactionservice.service;

import com.tien.interactionservice.dto.PageResponse;
import com.tien.interactionservice.dto.request.CreateLikeRequest;
import com.tien.interactionservice.dto.response.LikeResponse;
import com.tien.interactionservice.dto.response.ProfileResponse;
import com.tien.interactionservice.entity.Like;
import com.tien.interactionservice.event.LikeEvent;
import com.tien.interactionservice.exception.AppException;
import com.tien.interactionservice.exception.ErrorCode;
import com.tien.interactionservice.mapper.LikeMapper;
import com.tien.interactionservice.repository.CommentRepository;
import com.tien.interactionservice.repository.LikeRepository;
import com.tien.interactionservice.repository.httpclient.PostClient;
import com.tien.interactionservice.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeService {
    LikeRepository likeRepository;
    CommentRepository commentRepository;
    PostClient postClient;
    ProfileClient profileClient;
    LikeMapper likeMapper;
    KafkaTemplate<String, Object> kafkaTemplate;

    private static final String LIKE_TOPIC = "like.events";

    @Transactional
    public LikeResponse createLike(CreateLikeRequest request) {
        String userId = getCurrentUserId();

        // Check if already liked
        if (request.getPostId() != null) {
            validatePostExists(request.getPostId());
            if (likeRepository.findByUserIdAndPostIdAndCommentIdIsNull(userId, request.getPostId()).isPresent()) {
                throw new AppException(ErrorCode.ALREADY_LIKED);
            }
        } else {
            if (commentRepository.findById(request.getCommentId()).isEmpty()) {
                throw new AppException(ErrorCode.COMMENT_NOT_FOUND);
            }
            if (likeRepository.findByUserIdAndCommentIdAndPostIdIsNull(userId, request.getCommentId()).isPresent()) {
                throw new AppException(ErrorCode.ALREADY_LIKED);
            }
        }

        Like like = Like.builder()
                .userId(userId)
                .postId(request.getPostId())
                .commentId(request.getCommentId())
                .build();

        like = likeRepository.save(like);

        // Publish event
        publishLikeEvent(like, "CREATED");

        return buildLikeResponse(like);
    }

    @Transactional
    public void unlike(String likeId) {
        String userId = getCurrentUserId();

        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new AppException(ErrorCode.LIKE_NOT_FOUND));

        if (!like.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        likeRepository.delete(like);

        // Publish event
        publishLikeEvent(like, "DELETED");
    }

    @Transactional
    public void unlikeByPost(String postId) {
        String userId = getCurrentUserId();

        Like like = likeRepository.findByUserIdAndPostIdAndCommentIdIsNull(userId, postId)
                .orElseThrow(() -> new AppException(ErrorCode.LIKE_NOT_FOUND));

        likeRepository.delete(like);

        // Publish event
        publishLikeEvent(like, "DELETED");
    }

    @Transactional
    public void unlikeByComment(String commentId) {
        String userId = getCurrentUserId();

        Like like = likeRepository.findByUserIdAndCommentIdAndPostIdIsNull(userId, commentId)
                .orElseThrow(() -> new AppException(ErrorCode.LIKE_NOT_FOUND));

        likeRepository.delete(like);

        // Publish event
        publishLikeEvent(like, "DELETED");
    }

    public PageResponse<LikeResponse> getLikesByPost(String postId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Like> likesPage = likeRepository.findByPostIdAndCommentIdIsNull(postId, pageable);

        List<LikeResponse> likeResponses = likesPage.getContent().stream()
                .map(this::buildLikeResponse)
                .collect(Collectors.toList());

        return PageResponse.<LikeResponse>builder()
                .content(likeResponses)
                .page(page)
                .size(size)
                .totalElements(likesPage.getTotalElements())
                .totalPages(likesPage.getTotalPages())
                .hasNext(likesPage.hasNext())
                .hasPrevious(likesPage.hasPrevious())
                .build();
    }

    public long getLikeCountByPost(String postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean isPostLiked(String postId) {
        String userId = getCurrentUserId();
        return likeRepository.findByUserIdAndPostIdAndCommentIdIsNull(userId, postId).isPresent();
    }

    private LikeResponse buildLikeResponse(Like like) {
        ProfileResponse profile = getProfile(like.getUserId());

        // Map các field có thể map từ entity
        LikeResponse response = likeMapper.toLikeResponse(like);
        
        // Enrich các field không thể map
        // Hiển thị họ + tên thay vì username
        if (profile != null) {
            String displayName = getDisplayName(profile.getFirstName(), profile.getLastName(), profile.getUsername());
            response.setUsername(displayName);
            response.setUserAvatar(profile.getAvatar());
        } else {
            response.setUsername(null);
            response.setUserAvatar(null);
        }
        
        return response;
    }

    private String getDisplayName(String firstName, String lastName, String username) {
        // Nếu có cả firstName và lastName, hiển thị "firstName lastName"
        if (firstName != null && !firstName.trim().isEmpty() && lastName != null && !lastName.trim().isEmpty()) {
            return (firstName.trim() + " " + lastName.trim()).trim();
        }
        // Nếu chỉ có lastName, hiển thị lastName (thường là username)
        else if (lastName != null && !lastName.trim().isEmpty()) {
            return lastName.trim();
        }
        // Nếu chỉ có firstName, hiển thị firstName
        else if (firstName != null && !firstName.trim().isEmpty()) {
            return firstName.trim();
        }
        // Fallback to username
        else {
            return username != null ? username : "";
        }
    }

    private void validatePostExists(String postId) {
        try {
            var response = postClient.checkPostExists(postId);
            if (response == null || response.getResult() == null || !response.getResult()) {
                throw new AppException(ErrorCode.POST_NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error validating post existence: {}", e.getMessage());
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }
    }

    private ProfileResponse getProfile(String userId) {
        try {
            return profileClient.getProfile(userId).getResult();
        } catch (Exception e) {
            log.error("Error getting profile for userId: {}", userId, e);
            return null;
        }
    }

    private void publishLikeEvent(Like like, String eventType) {
        try {
            LikeEvent event = LikeEvent.builder()
                    .likeId(like.getId())
                    .userId(like.getUserId())
                    .postId(like.getPostId())
                    .commentId(like.getCommentId())
                    .eventType(eventType)
                    .timestamp(Instant.now())
                    .build();
            kafkaTemplate.send(LIKE_TOPIC, event);
            log.info("Published like event: {} for likeId: {}", eventType, like.getId());
        } catch (Exception e) {
            log.error("Error publishing like event: {}", e.getMessage(), e);
        }
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

