package com.tien.chatservice.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.tien.chatservice.constant.ChatNotificationType;
import com.tien.chatservice.dto.request.ChatMessageRequest;
import com.tien.chatservice.dto.request.ChatNotification;
import com.tien.chatservice.dto.request.TypingNotification;
import com.tien.chatservice.dto.response.ChatMessageResponse;
import com.tien.chatservice.repository.ConversationRepository;
import com.tien.chatservice.service.ChatMessageService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketController {

    ChatMessageService chatMessageService;
    SimpMessagingTemplate messagingTemplate;
    ConversationRepository conversationRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
        String userId = null;
        try {
            if (principal == null || principal.getName() == null) {
                log.warn("Không tìm thấy principal khi sendMessage");
                return;
            }

            userId = principal.getName();

            // Đảm bảo Authentication được set vào SecurityContextHolder
            // (Interceptor đã set rồi, nhưng đảm bảo chắc chắn cho trường hợp edge case)
            if (principal instanceof org.springframework.security.core.Authentication) {
                org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .setAuthentication((org.springframework.security.core.Authentication) principal);
            }

            log.info(
                    "Nhận tin nhắn qua WebSocket từ user {}: conversationId={}, message={}",
                    userId,
                    request.getConversationId(),
                    request.getMessage());

            // Service sẽ tự lấy userId từ SecurityContextHolder (đã được set bởi interceptor)
            ChatMessageResponse response = chatMessageService.create(request);

            response.setMe(false);
            messagingTemplate.convertAndSend("/topic/conversation/" + request.getConversationId(), response);
        } catch (Exception e) {
            log.error("Lỗi khi gửi tin nhắn qua WebSocket: {}", e.getMessage(), e);
            if (userId != null) {
                try {
                    messagingTemplate.convertAndSendToUser(
                            userId, "/queue/errors", "Gửi tin nhắn thất bại: " + e.getMessage());
                } catch (Exception ex) {
                    log.error("Không thể gửi error notification: {}", ex.getMessage());
                }
            }
        }
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload TypingNotification notification, Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                log.warn("Không tìm thấy principal khi handleTyping");
                return;
            }

            String userId = principal.getName();

            // Validate conversationId
            if (notification.getConversationId() == null
                    || notification.getConversationId().trim().isEmpty()) {
                log.warn("Typing notification không có conversationId");
                return;
            }

            // Tạo biến final để sử dụng trong lambda
            final String finalUserId = userId;

            // Validate user is participant
            boolean isParticipant = conversationRepository
                    .findById(notification.getConversationId())
                    .map(conv -> conv.getParticipants().stream()
                            .anyMatch(p -> p.getUserId().equals(finalUserId)))
                    .orElse(false);

            if (!isParticipant) {
                log.warn(
                        "User {} cố gắng gửi typing notification cho conversation {} mà họ không tham gia",
                        userId,
                        notification.getConversationId());
                return;
            }

            // Override userId from request with authenticated user
            notification.setUserId(userId);

            log.debug("User {} đang gõ trong conversation {}", userId, notification.getConversationId());

            messagingTemplate.convertAndSend(
                    "/topic/conversation/" + notification.getConversationId() + "/typing", notification);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý typing notification: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/chat.addUser")
    public void handleUserJoin(@Payload ChatNotification notification, Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                log.warn("Không tìm thấy principal khi handleUserJoin");
                return;
            }

            String userId = principal.getName();

            // Validate conversationId
            if (notification.getConversationId() == null
                    || notification.getConversationId().trim().isEmpty()) {
                log.warn("User join notification không có conversationId");
                return;
            }

            // Tạo biến final để sử dụng trong lambda
            final String finalUserId = userId;

            // Validate user is participant
            boolean isParticipant = conversationRepository
                    .findById(notification.getConversationId())
                    .map(conv -> conv.getParticipants().stream()
                            .anyMatch(p -> p.getUserId().equals(finalUserId)))
                    .orElse(false);

            if (!isParticipant) {
                log.warn(
                        "User {} cố gắng tham gia conversation {} mà họ không phải thành viên",
                        userId,
                        notification.getConversationId());
                return;
            }

            // Override sender from request with authenticated user
            notification.setSender(userId);
            if (notification.getType() == null) {
                notification.setType(ChatNotificationType.JOIN);
            }

            log.info("User {} đã tham gia conversation {}", userId, notification.getConversationId());

            messagingTemplate.convertAndSend("/topic/conversation/" + notification.getConversationId(), notification);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý user join notification: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/chat.removeUser")
    public void handleUserLeave(@Payload ChatNotification notification, Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                log.warn("Không tìm thấy principal khi handleUserLeave");
                return;
            }

            String userId = principal.getName();

            // Validate conversationId
            if (notification.getConversationId() == null
                    || notification.getConversationId().trim().isEmpty()) {
                log.warn("User leave notification không có conversationId");
                return;
            }

            // Tạo biến final để sử dụng trong lambda
            final String finalUserId = userId;

            // Validate user is participant
            boolean isParticipant = conversationRepository
                    .findById(notification.getConversationId())
                    .map(conv -> conv.getParticipants().stream()
                            .anyMatch(p -> p.getUserId().equals(finalUserId)))
                    .orElse(false);

            if (!isParticipant) {
                log.warn(
                        "User {} cố gắng rời khỏi conversation {} mà họ không tham gia",
                        userId,
                        notification.getConversationId());
                return;
            }

            // Override sender from request with authenticated user
            notification.setSender(userId);
            notification.setType(ChatNotificationType.LEAVE);

            log.info("User {} đã rời khỏi conversation {}", userId, notification.getConversationId());

            messagingTemplate.convertAndSend("/topic/conversation/" + notification.getConversationId(), notification);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý user leave notification: {}", e.getMessage(), e);
        }
    }
}
