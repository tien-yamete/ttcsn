package com.tien.chatservice.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tien.chatservice.constant.ParticipantRole;
import com.tien.chatservice.dto.PageResponse;
import com.tien.chatservice.dto.request.ChatMessageRequest;
import com.tien.chatservice.dto.request.UpdateMessageRequest;
import com.tien.chatservice.dto.response.ChatMessageResponse;
import com.tien.chatservice.dto.response.ProfileResponse;
import com.tien.chatservice.entity.ChatMessage;
import com.tien.chatservice.entity.Conversation;
import com.tien.chatservice.entity.ParticipantInfo;
import com.tien.chatservice.exception.AppException;
import com.tien.chatservice.exception.ErrorCode;
import com.tien.chatservice.mapper.ChatMessageMapper;
import com.tien.chatservice.repository.ChatMessageRepository;
import com.tien.chatservice.repository.ConversationRepository;
import com.tien.chatservice.repository.httpclient.ProfileClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageService {

    private static final String SORT_FIELD_CREATED_DATE = "createdDate";

    ChatMessageRepository chatMessageRepository;

    ChatMessageMapper chatMessageMapper;

    ProfileClient profileClient;

    ConversationRepository conversationRepository;

    public ChatMessageResponse create(ChatMessageRequest request) {
        String userId = getCurrentUserId();
        Conversation conversation = validateConversationAccess(request.getConversationId(), userId);

        ProfileResponse userInfo = getProfileOrThrow(userId);
        ChatMessage chatMessage = buildChatMessage(request, userInfo, conversation, userId);

        chatMessageRepository.save(chatMessage);

        return toChatMessageResponse(chatMessage, userId);
    }

    public List<ChatMessageResponse> getMessages(String conversationId) {
        String userId = getCurrentUserId();
        validateConversationAccess(conversationId, userId);

        var messages = chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(conversationId);

        return messages.stream().map(msg -> toChatMessageResponse(msg, userId)).toList();
    }

    public PageResponse<ChatMessageResponse> getMessagesWithPagination(String conversationId, int page, int size) {
        String userId = getCurrentUserId();
        validateConversationAccess(conversationId, userId);

        Pageable pageable =
                PageRequest.of(page - 1, size, Sort.by(SORT_FIELD_CREATED_DATE).descending());
        Page<ChatMessage> messagePage =
                chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(conversationId, pageable);

        List<ChatMessageResponse> responses = messagePage.getContent().stream()
                .map(msg -> toChatMessageResponse(msg, userId))
                .toList();

        return PageResponse.<ChatMessageResponse>builder()
                .currentPage(page)
                .totalPages(messagePage.getTotalPages())
                .pageSize(size)
                .totalElements(messagePage.getTotalElements())
                .data(responses)
                .build();
    }

    public ChatMessageResponse getById(String messageId) {
        String userId = getCurrentUserId();
        ChatMessage message = chatMessageRepository
                .findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        validateConversationAccess(message.getConversationId(), userId);

        return toChatMessageResponse(message, userId);
    }

    @Transactional
    public ChatMessageResponse update(String messageId, UpdateMessageRequest request) {
        String userId = getCurrentUserId();
        ChatMessage message = findMessageOrThrow(messageId);

        // Kiểm tra người dùng có phải là chủ message không - phải kiểm tra trước
        validateSender(message, userId);
        
        // Kiểm tra người dùng có quyền truy cập conversation không
        validateConversationAccess(message.getConversationId(), userId);

        // Chỉ cho phép update nếu là chủ message
        message.setMessage(request.getMessage());
        message = chatMessageRepository.save(message);

        log.info("User {} đã cập nhật message {}", userId, messageId);
        return toChatMessageResponse(message, userId);
    }

    @Transactional
    public void delete(String messageId) {
        String userId = getCurrentUserId();
        ChatMessage message = findMessageOrThrow(messageId);

        // Kiểm tra quyền xóa: chủ message hoặc ADMIN của conversation
        validateDeletePermission(message, userId);
        
        // Kiểm tra người dùng có quyền truy cập conversation không
        validateConversationAccess(message.getConversationId(), userId);

        chatMessageRepository.delete(message);
        log.info("User {} đã xóa message {}", userId, messageId);
    }

    private String getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = auth.getName();
        if (userId == null || userId.trim().isEmpty()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userId;
    }

    private ChatMessage findMessageOrThrow(String messageId) {
        return chatMessageRepository
                .findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));
    }

    private ProfileResponse getProfileOrThrow(String userId) {
        var userInfoResponse = profileClient.getProfile(userId);
        if (Objects.isNull(userInfoResponse) || userInfoResponse.getResult() == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return userInfoResponse.getResult();
    }

    private ChatMessage buildChatMessage(
            ChatMessageRequest request, ProfileResponse userInfo, Conversation conversation, String userId) {
        ChatMessage chatMessage = chatMessageMapper.toChatMessage(request);
        
        // Lấy role của người gửi từ conversation
        ParticipantRole senderRole = getSenderRoleFromConversation(conversation, userId);
        
        chatMessage.setSender(buildParticipantInfo(userInfo, senderRole));
        chatMessage.setCreatedDate(Instant.now());
        return chatMessage;
    }

    private ParticipantRole getSenderRoleFromConversation(Conversation conversation, String userId) {
        return conversation.getParticipants().stream()
                .filter(participant -> userId.equals(participant.getUserId()))
                .findFirst()
                .map(ParticipantInfo::getRole)
                .orElse(ParticipantRole.MEMBER); // Default to MEMBER if not found
    }

    private ParticipantInfo buildParticipantInfo(ProfileResponse profile, ParticipantRole role) {
        return ParticipantInfo.builder()
                .userId(profile.getUserId())
                .username(profile.getUsername())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .avatar(profile.getAvatar())
                .role(role)
                .build();
    }

    private void validateSender(ChatMessage message, String userId) {
        if (message == null) {
            throw new AppException(ErrorCode.MESSAGE_NOT_FOUND);
        }
        
        if (message.getSender() == null) {
            log.error("Message {} không có thông tin sender", message.getId());
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        String senderId = message.getSender().getUserId();
        if (senderId == null || !senderId.equals(userId)) {
            log.warn("User {} cố gắng thao tác với message của user {}", userId, senderId);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void validateDeletePermission(ChatMessage message, String userId) {
        if (message == null) {
            throw new AppException(ErrorCode.MESSAGE_NOT_FOUND);
        }
        
        if (message.getSender() == null) {
            log.error("Message {} không có thông tin sender", message.getId());
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        String senderId = message.getSender().getUserId();
        
        // Kiểm tra nếu là chủ message
        if (senderId != null && senderId.equals(userId)) {
            return; // Cho phép xóa nếu là chủ message
        }
        
        // Nếu không phải chủ message, kiểm tra xem có phải ADMIN của conversation không
        Conversation conversation = conversationRepository
                .findById(message.getConversationId())
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
        
        boolean isAdmin = conversation.getParticipants().stream()
                .anyMatch(participant -> userId.equals(participant.getUserId())
                        && participant.getRole() == ParticipantRole.ADMIN);
        
        if (!isAdmin) {
            log.warn("User {} cố gắng xóa message {} nhưng không phải chủ message và không phải ADMIN", 
                    userId, message.getId());
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private Conversation validateConversationAccess(String conversationId, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new AppException(ErrorCode.USER_ID_REQUIRED);
        }

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        boolean hasAccess = conversation.getParticipants().stream()
                .anyMatch(participantInfo -> userId.equals(participantInfo.getUserId()));

        if (!hasAccess) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND);
        }

        return conversation;
    }

    private ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage, String userId) {
        var chatMessageResponse = chatMessageMapper.toChatMessageResponse(chatMessage);
        // Nếu userId là null, set me = false (an toàn)
        chatMessageResponse.setMe(
                userId != null && userId.equals(chatMessage.getSender().getUserId()));
        return chatMessageResponse;
    }
}
