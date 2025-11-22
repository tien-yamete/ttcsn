package com.tien.chatservice.service;

import com.tien.chatservice.dto.PageResponse;
import com.tien.chatservice.dto.request.ChatMessageRequest;
import com.tien.chatservice.dto.request.UpdateMessageRequest;
import com.tien.chatservice.dto.response.ChatMessageResponse;
import com.tien.chatservice.dto.response.ProfileResponse;
import com.tien.chatservice.entity.ChatMessage;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

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
        validateConversationAccess(request.getConversationId(), userId);

        ProfileResponse userInfo = getProfileOrThrow(userId);
        ChatMessage chatMessage = buildChatMessage(request, userInfo);
        chatMessageRepository.save(chatMessage);

        return toChatMessageResponse(chatMessage, userId);
    }

    public List<ChatMessageResponse> getMessages(String conversationId) {
        String userId = getCurrentUserId();
        validateConversationAccess(conversationId, userId);

        var messages = chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(conversationId);

        return messages.stream().map(msg -> toChatMessageResponse(msg, userId)).toList();
    }

    public PageResponse<ChatMessageResponse> getMessagesWithPagination(
            String conversationId, int page, int size) {
        String userId = getCurrentUserId();
        validateConversationAccess(conversationId, userId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(SORT_FIELD_CREATED_DATE).descending());
        Page<ChatMessage> messagePage = chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(
                conversationId, pageable);

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
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        validateConversationAccess(message.getConversationId(), userId);

        return toChatMessageResponse(message, userId);
    }

    @Transactional
    public ChatMessageResponse update(String messageId, UpdateMessageRequest request) {
        String userId = getCurrentUserId();
        ChatMessage message = findMessageOrThrow(messageId);
        
        validateSender(message, userId);
        validateConversationAccess(message.getConversationId(), userId);

        message.setMessage(request.getMessage());
        message = chatMessageRepository.save(message);

        return toChatMessageResponse(message, userId);
    }

    @Transactional
    public void delete(String messageId) {
        String userId = getCurrentUserId();
        ChatMessage message = findMessageOrThrow(messageId);
        
        validateSender(message, userId);
        validateConversationAccess(message.getConversationId(), userId);

        chatMessageRepository.delete(message);
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
        return chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));
    }

    private ProfileResponse getProfileOrThrow(String userId) {
        var userInfoResponse = profileClient.getProfile(userId);
        if (Objects.isNull(userInfoResponse) || userInfoResponse.getResult() == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return userInfoResponse.getResult();
    }

    private ChatMessage buildChatMessage(ChatMessageRequest request, ProfileResponse userInfo) {
        ChatMessage chatMessage = chatMessageMapper.toChatMessage(request);
        chatMessage.setSender(buildParticipantInfo(userInfo));
        chatMessage.setCreatedDate(Instant.now());
        return chatMessage;
    }

    private ParticipantInfo buildParticipantInfo(ProfileResponse profile) {
        return ParticipantInfo.builder()
                .userId(profile.getUserId())
                .username(profile.getUsername())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .avatar(profile.getAvatar())
                .role(com.tien.chatservice.constant.ParticipantRole.MEMBER) // Default role for message sender
                .build();
    }

    private void validateSender(ChatMessage message, String userId) {
        if (!message.getSender().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void validateConversationAccess(String conversationId, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalStateException("User ID is required for conversation access validation.");
        }

        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants()
                .stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
    }

    private ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage, String userId) {
        var chatMessageResponse = chatMessageMapper.toChatMessageResponse(chatMessage);
        // Nếu userId là null, set me = false (an toàn)
        chatMessageResponse.setMe(userId != null && userId.equals(chatMessage.getSender().getUserId()));
        return chatMessageResponse;
    }
}
