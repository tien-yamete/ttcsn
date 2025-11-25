package com.tien.chatservice.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tien.chatservice.dto.response.ReadReceiptResponse;
import com.tien.chatservice.entity.ChatMessage;
import com.tien.chatservice.entity.ReadReceipt;
import com.tien.chatservice.exception.AppException;
import com.tien.chatservice.exception.ErrorCode;
import com.tien.chatservice.mapper.ReadReceiptMapper;
import com.tien.chatservice.repository.ChatMessageRepository;
import com.tien.chatservice.repository.ConversationRepository;
import com.tien.chatservice.repository.ReadReceiptRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadReceiptService {

    ReadReceiptRepository readReceiptRepository;
    ChatMessageRepository chatMessageRepository;
    ConversationRepository conversationRepository;
    ReadReceiptMapper readReceiptMapper;

    @Transactional
    public ReadReceiptResponse markAsRead(String messageId) {
        String userId = getCurrentUserId();
        ChatMessage message = findMessageOrThrow(messageId);

        validateConversationAccess(message.getConversationId());
        validateNotSender(message, userId);

        ReadReceipt receipt = readReceiptRepository
                .findByMessageIdAndUserId(messageId, userId)
                .orElseGet(() -> createReadReceipt(messageId, message.getConversationId(), userId));

        return readReceiptMapper.toReadReceiptResponse(receipt);
    }

    public List<ReadReceiptResponse> getReadReceipts(String messageId) {
        ChatMessage message = findMessageOrThrow(messageId);
        validateConversationAccess(message.getConversationId());

        return readReceiptRepository.findAllByMessageId(messageId).stream()
                .map(readReceiptMapper::toReadReceiptResponse)
                .toList();
    }

    public long getUnreadCount(String conversationId) {
        String userId = getCurrentUserId();
        validateConversationAccess(conversationId);

        List<ChatMessage> allMessages =
                chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(conversationId);
        Set<String> readMessageIds =
                new HashSet<>(readReceiptRepository.findAllByConversationIdAndUserId(conversationId, userId).stream()
                        .map(ReadReceipt::getMessageId)
                        .toList());

        return allMessages.stream()
                .filter(msg -> !msg.getSender().getUserId().equals(userId))
                .filter(msg -> !readMessageIds.contains(msg.getId()))
                .count();
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private ChatMessage findMessageOrThrow(String messageId) {
        return chatMessageRepository
                .findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));
    }

    private ReadReceipt createReadReceipt(String messageId, String conversationId, String userId) {
        ReadReceipt newReceipt = ReadReceipt.builder()
                .messageId(messageId)
                .conversationId(conversationId)
                .userId(userId)
                .readAt(Instant.now())
                .build();
        return readReceiptRepository.save(newReceipt);
    }

    private void validateNotSender(ChatMessage message, String userId) {
        if (message.getSender().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void validateConversationAccess(String conversationId) {
        String userId = getCurrentUserId();

        conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants()
                .stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
    }
}
