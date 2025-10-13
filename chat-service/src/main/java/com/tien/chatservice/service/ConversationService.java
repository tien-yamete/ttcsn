package com.tien.chatservice.service;

import com.tien.chatservice.dto.request.ConversationRequest;
import com.tien.chatservice.dto.response.ConversationResponse;
import com.tien.chatservice.entity.Conversation;
import com.tien.chatservice.entity.ParticipantInfo;
import com.tien.chatservice.exception.AppException;
import com.tien.chatservice.exception.ErrorCode;
import com.tien.chatservice.mapper.ConversationMapper;
import com.tien.chatservice.repository.ConversationRepository;
import com.tien.chatservice.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    ProfileClient profileClient;

    ConversationMapper conversationMapper;

    public List<ConversationResponse> myConversations() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Conversation> conversations = conversationRepository.findAllByParticipantIdsContains(userId);

        return conversations.stream().map(this::toConversationResponse).toList();
    }

    public ConversationResponse create(ConversationRequest request) {
        return null;
    }

    private String generateParticipantHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);

        // SHA 256

        return stringJoiner.toString();
    }

    private ConversationResponse toConversationResponse(Conversation conversation) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        ConversationResponse conversationResponse = conversationMapper.toConversationResponse(conversation);

        conversation.getParticipants().stream()
                .filter(participantInfo -> !participantInfo.getUserId().equals(currentUserId))
                .findFirst().ifPresent(participantInfo -> {
                    conversationResponse.setConversationName(participantInfo.getUsername());
                    conversationResponse.setConversationAvatar(participantInfo.getAvatar());
                });

        return conversationResponse;
    }
}
