package com.tien.chatservice.service;

import com.tien.chatservice.constant.TypeConversation;
import com.tien.chatservice.dto.request.AddParticipantRequest;
import com.tien.chatservice.dto.request.ConversationRequest;
import com.tien.chatservice.dto.request.UpdateConversationRequest;
import com.tien.chatservice.dto.response.ConversationResponse;
import com.tien.chatservice.dto.response.ProfileResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

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
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Validate conversation type
        if (request.getTypeConversation() == null) {
            throw new AppException(ErrorCode.INVALID_CONVERSATION_TYPE);
        }
        
        // For DIRECT: must have exactly 1 participant (plus current user = 2 total)
        if (request.getTypeConversation() == TypeConversation.DIRECT) {
            if (request.getParticipantIds().size() != 1) {
                throw new AppException(ErrorCode.INVALID_CONVERSATION_TYPE);
            }
        }
        
        // For GROUP: must have at least 1 participant (plus current user = 2+ total)
        if (request.getTypeConversation() == TypeConversation.GROUP) {
            if (request.getParticipantIds().isEmpty()) {
                throw new AppException(ErrorCode.INVALID_CONVERSATION_TYPE);
            }
        }
        
        // Get current user info
        var userInfoResponse = profileClient.getProfile(currentUserId);
        if (Objects.isNull(userInfoResponse)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        var userInfo = userInfoResponse.getResult();
        
        // Get all participant profiles
        List<String> allParticipantIds = new ArrayList<>(request.getParticipantIds());
        // Remove current user from list if present
        allParticipantIds.remove(currentUserId);
        
        var profilesResponse = profileClient.getProfiles(allParticipantIds);
        if (profilesResponse == null || profilesResponse.getResult() == null || profilesResponse.getResult().isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        
        List<ProfileResponse> participantProfiles = profilesResponse.getResult();
        
        // Build participant list
        List<ParticipantInfo> participantInfos = new ArrayList<>();
        
        // Add current user
        participantInfos.add(ParticipantInfo.builder()
                .userId(userInfo.getUserId())
                .username(userInfo.getUsername())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .avatar(userInfo.getAvatar())
                .build());
        
        // Add other participants
        participantInfos.addAll(participantProfiles.stream()
                .map(profile -> ParticipantInfo.builder()
                        .userId(profile.getUserId())
                        .username(profile.getUsername())
                        .firstName(profile.getFirstName())
                        .lastName(profile.getLastName())
                        .avatar(profile.getAvatar())
                        .build())
                .collect(Collectors.toList()));
        
        // Generate hash for DIRECT conversations (sorted user IDs)
        // For GROUP, use a unique hash based on all participants
        String userIdHash;
        if (request.getTypeConversation() == TypeConversation.DIRECT) {
            var sortedIds = participantInfos.stream()
                    .map(ParticipantInfo::getUserId)
                    .sorted()
                    .toList();
            userIdHash = generateParticipantHash(sortedIds);
        } else {
            // For GROUP, create hash from all participant IDs sorted
            var sortedIds = participantInfos.stream()
                    .map(ParticipantInfo::getUserId)
                    .sorted()
                    .toList();
            userIdHash = generateParticipantHash(sortedIds);
        }
        
        // For DIRECT: check if conversation already exists
        // For GROUP: always create new (or check if exact same participants exist)
        var conversation = conversationRepository.findByParticipantsHash(userIdHash)
                .filter(conv -> conv.getTypeConversation() == request.getTypeConversation())
                .orElseGet(() -> {
                    Conversation newConversation = Conversation.builder()
                            .typeConversation(request.getTypeConversation())
                            .participants(participantInfos)
                            .participantsHash(userIdHash)
                            .createdDate(Instant.now())
                            .modifiedDate(Instant.now())
                            .build();
                    return conversationRepository.save(newConversation);
                });
        
        return toConversationResponse(conversation);
    }

    public ConversationResponse getById(String conversationId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        validateParticipantAccess(conversation, userId);

        return toConversationResponse(conversation);
    }

    @Transactional
    public ConversationResponse update(String conversationId, UpdateConversationRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        // Only allow update for GROUP conversations
        if (conversation.getTypeConversation() != TypeConversation.GROUP) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        validateParticipantAccess(conversation, userId);

        if (request.getConversationName() != null && !request.getConversationName().trim().isEmpty()) {
            conversation.setConversationName(request.getConversationName());
        }
        if (request.getConversationAvatar() != null) {
            conversation.setConversationAvatar(request.getConversationAvatar());
        }
        conversation.setModifiedDate(Instant.now());

        conversation = conversationRepository.save(conversation);
        return toConversationResponse(conversation);
    }

    @Transactional
    public void delete(String conversationId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        validateParticipantAccess(conversation, userId);

        conversationRepository.delete(conversation);
    }

    @Transactional
    public ConversationResponse addParticipants(String conversationId, AddParticipantRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        // Only allow for GROUP conversations
        if (conversation.getTypeConversation() != TypeConversation.GROUP) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        validateParticipantAccess(conversation, userId);

        // Get existing participant IDs
        List<String> existingParticipantIds = conversation.getParticipants().stream()
                .map(ParticipantInfo::getUserId)
                .collect(Collectors.toList());

        // Filter out already existing participants
        List<String> newParticipantIds = request.getParticipantIds().stream()
                .filter(id -> !existingParticipantIds.contains(id))
                .collect(Collectors.toList());

        if (newParticipantIds.isEmpty()) {
            return toConversationResponse(conversation);
        }

        // Get profiles for new participants
        var profilesResponse = profileClient.getProfiles(newParticipantIds);
        if (profilesResponse == null || profilesResponse.getResult() == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        List<ProfileResponse> newProfiles = profilesResponse.getResult();
        List<ParticipantInfo> newParticipants = newProfiles.stream()
                .map(profile -> ParticipantInfo.builder()
                        .userId(profile.getUserId())
                        .username(profile.getUsername())
                        .firstName(profile.getFirstName())
                        .lastName(profile.getLastName())
                        .avatar(profile.getAvatar())
                        .build())
                .collect(Collectors.toList());

        // Add new participants
        List<ParticipantInfo> updatedParticipants = new ArrayList<>(conversation.getParticipants());
        updatedParticipants.addAll(newParticipants);
        conversation.setParticipants(updatedParticipants);
        conversation.setModifiedDate(Instant.now());

        conversation = conversationRepository.save(conversation);
        return toConversationResponse(conversation);
    }

    @Transactional
    public ConversationResponse removeParticipant(String conversationId, String participantId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        // Only allow for GROUP conversations
        if (conversation.getTypeConversation() != TypeConversation.GROUP) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        validateParticipantAccess(conversation, userId);

        // Cannot remove yourself from group (use leave instead)
        if (participantId.equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Remove participant
        List<ParticipantInfo> updatedParticipants = conversation.getParticipants().stream()
                .filter(p -> !p.getUserId().equals(participantId))
                .collect(Collectors.toList());

        if (updatedParticipants.size() == conversation.getParticipants().size()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        conversation.setParticipants(updatedParticipants);
        conversation.setModifiedDate(Instant.now());

        conversation = conversationRepository.save(conversation);
        return toConversationResponse(conversation);
    }

    @Transactional
    public void leave(String conversationId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        validateParticipantAccess(conversation, userId);

        // For DIRECT conversation, delete it
        if (conversation.getTypeConversation() == TypeConversation.DIRECT) {
            conversationRepository.delete(conversation);
            return;
        }

        // For GROUP conversation, remove user from participants
        List<ParticipantInfo> updatedParticipants = conversation.getParticipants().stream()
                .filter(p -> !p.getUserId().equals(userId))
                .collect(Collectors.toList());

        // If only one participant left, delete conversation
        if (updatedParticipants.size() <= 1) {
            conversationRepository.delete(conversation);
        } else {
            conversation.setParticipants(updatedParticipants);
            conversation.setModifiedDate(Instant.now());
            conversationRepository.save(conversation);
        }
    }

    private String generateParticipantHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

    private ConversationResponse toConversationResponse(Conversation conversation) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        ConversationResponse conversationResponse = conversationMapper.toConversationResponse(conversation);

        // For GROUP conversations, use conversation name/avatar if set
        if (conversation.getTypeConversation() == TypeConversation.GROUP) {
            if (conversation.getConversationName() != null) {
                conversationResponse.setConversationName(conversation.getConversationName());
            }
            if (conversation.getConversationAvatar() != null) {
                conversationResponse.setConversationAvatar(conversation.getConversationAvatar());
            }
        } else {
            // For DIRECT conversations, use the other participant's info
            conversation.getParticipants().stream()
                    .filter(participantInfo -> !participantInfo.getUserId().equals(currentUserId))
                    .findFirst().ifPresent(participantInfo -> {
                        conversationResponse.setConversationName(participantInfo.getUsername());
                        conversationResponse.setConversationAvatar(participantInfo.getAvatar());
                    });
        }

        return conversationResponse;
    }

    private void validateParticipantAccess(Conversation conversation, String userId) {
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUserId().equals(userId));
        if (!isParticipant) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
