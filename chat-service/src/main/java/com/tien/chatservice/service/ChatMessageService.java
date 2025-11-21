package com.tien.chatservice.service;

import com.tien.chatservice.dto.PageResponse;
import com.tien.chatservice.dto.request.ChatMessageRequest;
import com.tien.chatservice.dto.request.UpdateMessageRequest;
import com.tien.chatservice.dto.response.ChatMessageResponse;
import com.tien.chatservice.dto.response.ProfileResponse;
import com.tien.chatservice.dto.response.ReadReceiptResponse;
import com.tien.chatservice.entity.ChatMessage;
import com.tien.chatservice.entity.ParticipantInfo;
import com.tien.chatservice.entity.ReadReceipt;
import com.tien.chatservice.exception.AppException;
import com.tien.chatservice.exception.ErrorCode;
import com.tien.chatservice.mapper.ChatMessageMapper;
import com.tien.chatservice.repository.ChatMessageRepository;
import com.tien.chatservice.repository.ConversationRepository;
import com.tien.chatservice.repository.ReadReceiptRepository;
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

    ChatMessageRepository chatMessageRepository;

    ChatMessageMapper chatMessageMapper;

    ProfileClient profileClient;

    ConversationRepository conversationRepository;

    ReadReceiptRepository readReceiptRepository;

    public ChatMessageResponse create(ChatMessageRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants()
                .stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        var userInfoResponse = profileClient.getProfile(userId);
        if (Objects.isNull(userInfoResponse)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        ProfileResponse userInfo = userInfoResponse.getResult();

        ChatMessage chatMessage = chatMessageMapper.toChatMessage(request);

        chatMessage.setSender(ParticipantInfo.builder()
                        .userId(userInfo.getUserId())
                        .username(userInfo.getUsername())
                        .firstName(userInfo.getFirstName())
                        .lastName(userInfo.getLastName())
                        .avatar(userInfo.getAvatar())
                .build());

        chatMessage.setCreatedDate(Instant.now());
        chatMessageRepository.save(chatMessage);

        return toChatMessageResponse(chatMessage);
    }

    public List<ChatMessageResponse> getMessages(String conversationId) {
        validateConversationAccess(conversationId);

        var messages = chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(conversationId);

        return messages.stream().map(this::toChatMessageResponse).toList();
    }

    public PageResponse<ChatMessageResponse> getMessagesWithPagination(
            String conversationId, int page, int size) {
        validateConversationAccess(conversationId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        Page<ChatMessage> messagePage = chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(
                conversationId, pageable);

        List<ChatMessageResponse> responses = messagePage.getContent().stream()
                .map(this::toChatMessageResponse)
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
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        validateConversationAccess(message.getConversationId());

        return toChatMessageResponse(message);
    }

    @Transactional
    public ChatMessageResponse update(String messageId, UpdateMessageRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        // Check if user is the sender
        if (!message.getSender().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        validateConversationAccess(message.getConversationId());

        message.setMessage(request.getMessage());
        message = chatMessageRepository.save(message);

        return toChatMessageResponse(message);
    }

    @Transactional
    public void delete(String messageId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        // Check if user is the sender
        if (!message.getSender().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        validateConversationAccess(message.getConversationId());

        // Delete associated read receipts
        readReceiptRepository.deleteAll(readReceiptRepository.findAllByMessageId(messageId));

        chatMessageRepository.delete(message);
    }

    @Transactional
    public ReadReceiptResponse markAsRead(String messageId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        validateConversationAccess(message.getConversationId());

        // Don't mark own messages as read
        if (message.getSender().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        ReadReceipt receipt = readReceiptRepository.findByMessageIdAndUserId(messageId, userId)
                .orElseGet(() -> {
                    ReadReceipt newReceipt = ReadReceipt.builder()
                            .messageId(messageId)
                            .conversationId(message.getConversationId())
                            .userId(userId)
                            .readAt(Instant.now())
                            .build();
                    return readReceiptRepository.save(newReceipt);
                });

        return ReadReceiptResponse.builder()
                .id(receipt.getId())
                .messageId(receipt.getMessageId())
                .conversationId(receipt.getConversationId())
                .userId(receipt.getUserId())
                .readAt(receipt.getReadAt())
                .build();
    }

    public List<ReadReceiptResponse> getReadReceipts(String messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        validateConversationAccess(message.getConversationId());

        return readReceiptRepository.findAllByMessageId(messageId).stream()
                .map(receipt -> ReadReceiptResponse.builder()
                        .id(receipt.getId())
                        .messageId(receipt.getMessageId())
                        .conversationId(receipt.getConversationId())
                        .userId(receipt.getUserId())
                        .readAt(receipt.getReadAt())
                        .build())
                .toList();
    }

    public long getUnreadCount(String conversationId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        validateConversationAccess(conversationId);

        // Count messages in conversation that are not from current user and not read
        List<ChatMessage> allMessages = chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(conversationId);
        List<String> readMessageIds = readReceiptRepository.findAllByConversationIdAndUserId(conversationId, userId)
                .stream()
                .map(ReadReceipt::getMessageId)
                .toList();

        return allMessages.stream()
                .filter(msg -> !msg.getSender().getUserId().equals(userId))
                .filter(msg -> !readMessageIds.contains(msg.getId()))
                .count();
    }

    private void validateConversationAccess(String conversationId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants()
                .stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
    }

    private ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var chatMessageResponse = chatMessageMapper.toChatMessageResponse(chatMessage);

        chatMessageResponse.setMe(userId.equals(chatMessage.getSender().getUserId()));

        return chatMessageResponse;
    }
}
