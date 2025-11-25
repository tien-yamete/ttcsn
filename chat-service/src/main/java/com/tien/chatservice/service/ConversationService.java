package com.tien.chatservice.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tien.chatservice.constant.ParticipantRole;
import com.tien.chatservice.constant.TypeConversation;
import com.tien.chatservice.dto.request.AddAdminRequest;
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

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {

    static String PARTICIPANT_HASH_DELIMITER = "_";
    static int DIRECT_CONVERSATION_PARTICIPANT_COUNT = 1;
    static int MIN_GROUP_PARTICIPANTS_AFTER_LEAVE = 2;

    ConversationRepository conversationRepository;
    ProfileClient profileClient;

    ConversationMapper conversationMapper;

    public List<ConversationResponse> myConversations() {
        String userId = getCurrentUserId();
        List<Conversation> conversations = conversationRepository.findAllByParticipantIdsContains(userId);
        return conversations.stream().map(this::toConversationResponse).toList();
    }

    public ConversationResponse create(ConversationRequest request) {
        String currentUserId = getCurrentUserId();
        validateConversationRequest(request);

        ProfileResponse currentUserInfo = getProfileOrThrow(currentUserId);
        List<String> otherParticipantIds = getOtherParticipantIds(request.getParticipantIds(), currentUserId);
        List<ProfileResponse> participantProfiles = getProfilesOrThrow(otherParticipantIds);

        // Tự động xác định loại conversation: 1 người khác = DIRECT, nhiều hơn = GROUP
        TypeConversation typeConversation =
                (otherParticipantIds.size() == 1) ? TypeConversation.DIRECT : TypeConversation.GROUP;

        List<ParticipantInfo> participantInfos =
                buildParticipantInfos(currentUserInfo, participantProfiles, typeConversation, true);
        String userIdHash = generateParticipantHash(participantInfos);

        Conversation conversation = findOrCreateConversation(typeConversation, participantInfos, userIdHash);
        return toConversationResponse(conversation);
    }

    public ConversationResponse getById(String conversationId) {
        String userId = getCurrentUserId();
        Conversation conversation = findConversationOrThrow(conversationId);
        validateParticipantAccess(conversation, userId);
        return toConversationResponse(conversation);
    }

    @Transactional
    public ConversationResponse updateConversation(String conversationId, UpdateConversationRequest request) {
        String userId = getCurrentUserId();
        Conversation conversation = findConversationOrThrow(conversationId);

        validateParticipantAccess(conversation, userId);

        // DIRECT conversation: tất cả đều là admin nên có thể update
        // GROUP conversation: chỉ admin mới có thể update
        if (conversation.getTypeConversation() == TypeConversation.GROUP) {
            validateAdminPermission(conversation, userId);
        }

        updateConversationDetails(conversation, request);
        conversation = conversationRepository.save(conversation);
        return toConversationResponse(conversation);
    }

    @Transactional
    public void deleteConversation(String conversationId) {
        String userId = getCurrentUserId();
        Conversation conversation = findConversationOrThrow(conversationId);
        validateParticipantAccess(conversation, userId);

        // Chỉ admin mới có thể xóa GROUP conversation
        if (conversation.getTypeConversation() == TypeConversation.GROUP) {
            validateAdminPermission(conversation, userId);
        }

        conversationRepository.delete(conversation);
    }

    @Transactional
    public ConversationResponse addParticipants(String conversationId, AddParticipantRequest request) {
        String userId = getCurrentUserId();
        Conversation conversation = findConversationOrThrow(conversationId);

        validateGroupConversation(conversation);
        validateParticipantAccess(conversation, userId);
        validateAdminPermission(conversation, userId);
        validateNoDuplicateParticipantIds(request.getParticipantIds());

        Set<String> existingParticipantIds = conversation.getParticipants().stream()
                .map(ParticipantInfo::getUserId)
                .collect(Collectors.toSet());

        List<String> duplicateIds = request.getParticipantIds().stream()
                .filter(existingParticipantIds::contains)
                .collect(Collectors.toList());

        if (!duplicateIds.isEmpty()) {
            throw new AppException(ErrorCode.PARTICIPANT_ALREADY_EXISTS);
        }

        List<String> newParticipantIds = new ArrayList<>(request.getParticipantIds());

        if (newParticipantIds.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        List<ParticipantInfo> newParticipants =
                buildParticipantInfosFromProfiles(getProfilesOrThrow(newParticipantIds), false);

        List<ParticipantInfo> updatedParticipants = new ArrayList<>(conversation.getParticipants());
        updatedParticipants.addAll(newParticipants);
        conversation.setParticipants(updatedParticipants);
        conversation.setModifiedDate(Instant.now());

        conversation = conversationRepository.save(conversation);
        return toConversationResponse(conversation);
    }

    @Transactional
    public ConversationResponse removeParticipant(String conversationId, String participantId) {
        String userId = getCurrentUserId();
        Conversation conversation = findConversationOrThrow(conversationId);

        validateGroupConversation(conversation);
        validateParticipantAccess(conversation, userId);
        validateAdminPermission(conversation, userId);
        validateNotSelf(participantId, userId);

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
    public ConversationResponse promoteToAdmin(String conversationId, AddAdminRequest request) {
        String userId = getCurrentUserId();
        Conversation conversation = findConversationOrThrow(conversationId);

        validateGroupConversation(conversation);
        validateParticipantAccess(conversation, userId);
        validateAdminPermission(conversation, userId);
        validateNoDuplicateParticipantIds(request.getParticipantIds());

        Set<String> participantIds = conversation.getParticipants().stream()
                .map(ParticipantInfo::getUserId)
                .collect(Collectors.toSet());

        List<String> invalidIds = request.getParticipantIds().stream()
                .filter(id -> !participantIds.contains(id))
                .collect(Collectors.toList());

        if (!invalidIds.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        List<ParticipantInfo> updatedParticipants = conversation.getParticipants().stream()
                .map(p -> {
                    if (request.getParticipantIds().contains(p.getUserId())) {
                        return ParticipantInfo.builder()
                                .userId(p.getUserId())
                                .username(p.getUsername())
                                .firstName(p.getFirstName())
                                .lastName(p.getLastName())
                                .avatar(p.getAvatar())
                                .role(ParticipantRole.ADMIN)
                                .build();
                    }
                    return p;
                })
                .collect(Collectors.toList());

        conversation.setParticipants(updatedParticipants);
        conversation.setModifiedDate(Instant.now());
        conversation = conversationRepository.save(conversation);
        return toConversationResponse(conversation);
    }

    @Transactional
    public ConversationResponse demoteFromAdmin(String conversationId, String participantId) {
        String userId = getCurrentUserId();
        Conversation conversation = findConversationOrThrow(conversationId);

        validateGroupConversation(conversation);
        validateParticipantAccess(conversation, userId);
        validateAdminPermission(conversation, userId);
        validateNotSelf(participantId, userId);

        ParticipantInfo targetParticipant = conversation.getParticipants().stream()
                .filter(p -> p.getUserId().equals(participantId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (targetParticipant.getRole() != ParticipantRole.ADMIN) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        // Kiểm tra phải còn ít nhất 1 admin sau khi demote
        long adminCount = conversation.getParticipants().stream()
                .filter(p -> p.getRole() == ParticipantRole.ADMIN)
                .count();

        if (adminCount <= 1) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        List<ParticipantInfo> updatedParticipants = conversation.getParticipants().stream()
                .map(p -> {
                    if (p.getUserId().equals(participantId)) {
                        return ParticipantInfo.builder()
                                .userId(p.getUserId())
                                .username(p.getUsername())
                                .firstName(p.getFirstName())
                                .lastName(p.getLastName())
                                .avatar(p.getAvatar())
                                .role(ParticipantRole.MEMBER)
                                .build();
                    }
                    return p;
                })
                .collect(Collectors.toList());

        conversation.setParticipants(updatedParticipants);
        conversation.setModifiedDate(Instant.now());
        conversation = conversationRepository.save(conversation);
        return toConversationResponse(conversation);
    }

    @Transactional
    public void leaveConversation(String conversationId) {
        String userId = getCurrentUserId();
        Conversation conversation = findConversationOrThrow(conversationId);
        validateParticipantAccess(conversation, userId);

        if (conversation.getTypeConversation() == TypeConversation.DIRECT) {
            conversationRepository.delete(conversation);
            return;
        }

        List<ParticipantInfo> updatedParticipants = conversation.getParticipants().stream()
                .filter(p -> !p.getUserId().equals(userId))
                .collect(Collectors.toList());

        if (updatedParticipants.size() < MIN_GROUP_PARTICIPANTS_AFTER_LEAVE) {
            conversationRepository.delete(conversation);
        } else {
            conversation.setParticipants(updatedParticipants);
            conversation.setModifiedDate(Instant.now());
            conversationRepository.save(conversation);
        }
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Conversation findConversationOrThrow(String conversationId) {
        return conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
    }

    private ProfileResponse getProfileOrThrow(String userId) {
        var userInfoResponse = profileClient.getProfile(userId);
        if (Objects.isNull(userInfoResponse) || userInfoResponse.getResult() == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return userInfoResponse.getResult();
    }

    private List<ProfileResponse> getProfilesOrThrow(List<String> userIds) {
        if (userIds.isEmpty()) {
            return List.of();
        }
        var profilesResponse = profileClient.getProfiles(userIds);
        if (profilesResponse == null
                || profilesResponse.getResult() == null
                || profilesResponse.getResult().isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return profilesResponse.getResult();
    }

    private void validateConversationRequest(ConversationRequest request) {
        // Chỉ cần kiểm tra có participants không, type sẽ được xác định tự động
        if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_CONVERSATION_TYPE);
        }
    }

    private List<String> getOtherParticipantIds(List<String> participantIds, String currentUserId) {
        List<String> otherIds = new ArrayList<>(participantIds);
        otherIds.remove(currentUserId);
        return otherIds;
    }

    private List<ParticipantInfo> buildParticipantInfos(
            ProfileResponse currentUser,
            List<ProfileResponse> otherProfiles,
            TypeConversation typeConversation,
            boolean isCreator) {
        List<ParticipantInfo> participantInfos = new ArrayList<>();

        // DIRECT conversation: tất cả participants đều là ADMIN
        // GROUP conversation: chỉ creator là ADMIN, các thành viên khác là MEMBER
        if (typeConversation == TypeConversation.DIRECT) {
            participantInfos.add(buildParticipantInfo(currentUser, ParticipantRole.ADMIN));
            participantInfos.addAll(buildParticipantInfosFromProfiles(otherProfiles, true));
        } else {
            // GROUP conversation
            ParticipantRole creatorRole = isCreator ? ParticipantRole.ADMIN : ParticipantRole.MEMBER;
            participantInfos.add(buildParticipantInfo(currentUser, creatorRole));
            participantInfos.addAll(buildParticipantInfosFromProfiles(otherProfiles, false));
        }

        return participantInfos;
    }

    private List<ParticipantInfo> buildParticipantInfosFromProfiles(List<ProfileResponse> profiles, boolean isAdmin) {
        ParticipantRole role = isAdmin ? ParticipantRole.ADMIN : ParticipantRole.MEMBER;
        return profiles.stream()
                .map(profile -> buildParticipantInfo(profile, role))
                .collect(Collectors.toList());
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

    private String generateParticipantHash(List<ParticipantInfo> participantInfos) {
        List<String> sortedIds = participantInfos.stream()
                .map(ParticipantInfo::getUserId)
                .sorted()
                .toList();
        return generateParticipantHashFromIds(sortedIds);
    }

    private String generateParticipantHashFromIds(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner(PARTICIPANT_HASH_DELIMITER);
        ids.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

    private Conversation findOrCreateConversation(
            TypeConversation typeConversation, List<ParticipantInfo> participantInfos, String userIdHash) {
        return conversationRepository
                .findByParticipantsHash(userIdHash)
                .filter(conv -> conv.getTypeConversation() == typeConversation)
                .map(conv -> {
                    // Đảm bảo role đúng cho conversation cũ
                    // DIRECT: tất cả phải là ADMIN
                    // GROUP: giữ nguyên role hiện tại
                    if (conv.getTypeConversation() == TypeConversation.DIRECT) {
                        List<ParticipantInfo> normalizedParticipants = conv.getParticipants().stream()
                                .map(p -> ParticipantInfo.builder()
                                        .userId(p.getUserId())
                                        .username(p.getUsername())
                                        .firstName(p.getFirstName())
                                        .lastName(p.getLastName())
                                        .avatar(p.getAvatar())
                                        .role(ParticipantRole.ADMIN) // DIRECT: tất cả đều là ADMIN
                                        .build())
                                .collect(Collectors.toList());
                        conv.setParticipants(normalizedParticipants);
                        conv.setModifiedDate(Instant.now());
                        return conversationRepository.save(conv);
                    }
                    return conv;
                })
                .orElseGet(() -> createNewConversation(typeConversation, participantInfos, userIdHash));
    }

    private Conversation createNewConversation(
            TypeConversation typeConversation, List<ParticipantInfo> participantInfos, String userIdHash) {
        Conversation newConversation = Conversation.builder()
                .typeConversation(typeConversation)
                .participants(participantInfos)
                .participantsHash(userIdHash)
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();
        return conversationRepository.save(newConversation);
    }

    private void validateGroupConversation(Conversation conversation) {
        if (conversation.getTypeConversation() != TypeConversation.GROUP) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void validateNotSelf(String participantId, String userId) {
        if (participantId.equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void validateNoDuplicateParticipantIds(List<String> participantIds) {
        Set<String> uniqueIds = new HashSet<>(participantIds);
        if (uniqueIds.size() != participantIds.size()) {
            throw new AppException(ErrorCode.DUPLICATE_PARTICIPANT_IDS);
        }
    }

    private void validateAdminPermission(Conversation conversation, String userId) {
        // DIRECT conversation: tất cả participants đều là admin, không cần validate
        if (conversation.getTypeConversation() == TypeConversation.DIRECT) {
            return;
        }

        // GROUP conversation: chỉ admin mới có quyền
        boolean isAdmin = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUserId().equals(userId) && p.getRole() == ParticipantRole.ADMIN);
        if (!isAdmin) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void updateConversationDetails(Conversation conversation, UpdateConversationRequest request) {
        if (request.getConversationName() != null
                && !request.getConversationName().trim().isEmpty()) {
            conversation.setConversationName(request.getConversationName());
        }
        if (request.getConversationAvatar() != null) {
            conversation.setConversationAvatar(request.getConversationAvatar());
        }
        conversation.setModifiedDate(Instant.now());
    }

    private ConversationResponse toConversationResponse(Conversation conversation) {
        String currentUserId = getCurrentUserId();
        ConversationResponse conversationResponse = conversationMapper.toConversationResponse(conversation);

        if (conversation.getTypeConversation() == TypeConversation.GROUP) {
            enrichGroupConversationResponse(conversationResponse, conversation);
        } else {
            enrichDirectConversationResponse(conversationResponse, conversation, currentUserId);
        }

        return conversationResponse;
    }

    private void enrichGroupConversationResponse(ConversationResponse response, Conversation conversation) {
        if (conversation.getConversationName() != null) {
            response.setConversationName(conversation.getConversationName());
        }
        if (conversation.getConversationAvatar() != null) {
            response.setConversationAvatar(conversation.getConversationAvatar());
        }
    }

    private void enrichDirectConversationResponse(
            ConversationResponse response, Conversation conversation, String currentUserId) {
        conversation.getParticipants().stream()
                .filter(participantInfo -> !participantInfo.getUserId().equals(currentUserId))
                .findFirst()
                .ifPresent(participantInfo -> {
                    // Hiển thị họ + tên thay vì username
                    String displayName = getDisplayName(
                            participantInfo.getFirstName(),
                            participantInfo.getLastName(),
                            participantInfo.getUsername());
                    response.setConversationName(displayName);
                    response.setConversationAvatar(participantInfo.getAvatar());
                });
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

    private void validateParticipantAccess(Conversation conversation, String userId) {
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getUserId().equals(userId));
        if (!isParticipant) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
