package com.tien.interactionservice.service;

import com.tien.interactionservice.dto.PageResponse;
import com.tien.interactionservice.dto.request.CreateCommentRequest;
import com.tien.interactionservice.dto.request.UpdateCommentRequest;
import com.tien.interactionservice.dto.response.CommentResponse;
import com.tien.interactionservice.dto.response.ProfileResponse;
import com.tien.interactionservice.entity.Comment;
import com.tien.interactionservice.event.CommentEvent;
import com.tien.interactionservice.exception.AppException;
import com.tien.interactionservice.exception.ErrorCode;
import com.tien.interactionservice.mapper.CommentMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    LikeRepository likeRepository;
    PostClient postClient;
    ProfileClient profileClient;
    CommentMapper commentMapper;

    KafkaTemplate<String, Object> kafkaTemplate;

    private static final String COMMENT_TOPIC = "comment.events";

    @Transactional
    public CommentResponse createComment(CreateCommentRequest request) {
        String userId = getCurrentUserId();

        // Validate post exists
        validatePostExists(request.getPostId());

        // If parent comment exists, validate it
        if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
            Comment parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
            if (!parent.getPostId().equals(request.getPostId())) {
                throw new AppException(ErrorCode.INVALID_PARENT_COMMENT);
            }
        }

        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .userId(userId)
                .content(request.getContent())
                .parentCommentId(request.getParentCommentId())
                .build();

        comment = commentRepository.save(comment);

        // Publish event
        publishCommentEvent(comment, "CREATED");

        return buildCommentResponse(comment, userId);
    }

    public PageResponse<CommentResponse> getCommentsByPost(String postId, int page, int size) {
        String userId = getCurrentUserId();

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Comment> commentsPage = commentRepository.findByPostIdAndParentCommentIdIsNull(postId, pageable);

        List<CommentResponse> commentResponses = commentsPage.getContent().stream()
                .map(comment -> buildCommentResponse(comment, userId))
                .collect(Collectors.toList());

        // Load replies for each comment
        commentResponses.forEach(commentResponse -> {
            List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(commentResponse.getId());
            List<CommentResponse> replyResponses = replies.stream()
                    .map(reply -> buildCommentResponse(reply, userId))
                    .collect(Collectors.toList());
            commentResponse.setReplies(replyResponses);
            commentResponse.setReplyCount(replies.size());
        });

        return PageResponse.<CommentResponse>builder()
                .content(commentResponses)
                .page(page)
                .size(size)
                .totalElements(commentsPage.getTotalElements())
                .totalPages(commentsPage.getTotalPages())
                .hasNext(commentsPage.hasNext())
                .hasPrevious(commentsPage.hasPrevious())
                .build();
    }

    @Transactional
    public CommentResponse updateComment(String commentId, UpdateCommentRequest request) {
        String userId = getCurrentUserId();

        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);

        // Publish event
        publishCommentEvent(comment, "UPDATED");

        return buildCommentResponse(comment, userId);
    }

    @Transactional
    public void deleteComment(String commentId) {
        String userId = getCurrentUserId();

        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        // Delete all replies first
        List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(commentId);
        commentRepository.deleteAll(replies);

        // Delete likes on this comment
        likeRepository.deleteByCommentId(commentId);

        // Delete the comment
        commentRepository.delete(comment);

        // Publish event
        publishCommentEvent(comment, "DELETED");
    }

    private CommentResponse buildCommentResponse(Comment comment, String currentUserId) {
        ProfileResponse profile = getProfile(comment.getUserId());

        long likeCount = likeRepository.countByCommentId(comment.getId());
        boolean isLiked = likeRepository.findByUserIdAndCommentIdAndPostIdIsNull(currentUserId, comment.getId()).isPresent();

        // Map các field có thể map từ entity
        CommentResponse response = commentMapper.toCommentResponse(comment);
        
        // Enrich các field không thể map
        response.setUsername(profile != null ? profile.getUsername() : null);
        response.setUserAvatar(profile != null ? profile.getAvatar() : null);
        response.setReplies(new ArrayList<>());
        response.setReplyCount(0);
        response.setLikeCount((int) likeCount);
        response.setIsLiked(isLiked);
        
        return response;
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

    private void publishCommentEvent(Comment comment, String eventType) {
        try {
            CommentEvent event = CommentEvent.builder()
                    .commentId(comment.getId())
                    .postId(comment.getPostId())
                    .userId(comment.getUserId())
                    .eventType(eventType)
                    .timestamp(Instant.now())
                    .build();
            kafkaTemplate.send(COMMENT_TOPIC, event);
            log.info("Published comment event: {} for commentId: {}", eventType, comment.getId());
        } catch (Exception e) {
            log.error("Error publishing comment event: {}", e.getMessage(), e);
        }
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

