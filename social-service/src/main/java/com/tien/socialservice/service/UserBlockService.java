package com.tien.socialservice.service;

import com.tien.socialservice.dto.PageResponse;
import com.tien.socialservice.dto.response.UserBlockResponse;
import com.tien.socialservice.entity.UserBlock;
import com.tien.socialservice.exception.AppException;
import com.tien.socialservice.exception.ErrorCode;
import com.tien.socialservice.mapper.UserBlockMapper;
import com.tien.socialservice.repository.FollowRepository;
import com.tien.socialservice.repository.FriendshipRepository;
import com.tien.socialservice.repository.UserBlockRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserBlockService {
    UserBlockRepository userBlockRepository;

    FollowRepository followRepository;

    FriendshipRepository friendshipRepository;

    UserBlockMapper userBlockMapper;

    @Transactional
    public UserBlockResponse blockUser(String blockerId, String blockedId) {
        if(blockerId.equals(blockedId)){
            throw new AppException(ErrorCode.CANNOT_BLOCK_SELF);
        }

        // Check if already blocked
        if(userBlockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)){
            throw new AppException(ErrorCode.USER_ALREADY_BLOCKED);
        }

        //remove follow if exists
        followRepository.findByFollowerIdAndFollowingId(blockerId, blockedId)
                .ifPresent(followRepository::delete);
        followRepository.findByFollowerIdAndFollowingId(blockedId, blockerId)
                .ifPresent(followRepository::delete);

        //remove friendship if exists
        friendshipRepository.findByUserIdAndFriendId(blockerId, blockedId)
                .ifPresent(friendshipRepository::delete);
        friendshipRepository.findByUserIdAndFriendId(blockedId, blockerId)
                .ifPresent(friendshipRepository::delete);

        UserBlock userBlock = UserBlock.builder()
                .blockedId(blockedId)
                .blockerId(blockerId)
                .build();

        userBlock = userBlockRepository.save(userBlock);

        log.info("User {} blocked user {}", blockerId, blockedId);

        return userBlockMapper.toUserBlockResponse(userBlock);
    }

    @Transactional
    public void unblockUser(String blockerId, String blockedId) {
        UserBlock userBlock = userBlockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_BLOCKED));

        userBlockRepository.delete(userBlock);

        log.info("User {} unblocked user {}", blockerId, blockedId);
    }

    public PageResponse<UserBlockResponse> getBlockedUsers(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());

        var PageData = userBlockRepository.findBlockedUsersByUserId(userId, pageable);

        var userBlockResponses = PageData.getContent().stream()
                .map(userBlockMapper::toUserBlockResponse)
                .toList();

        return PageResponse.<UserBlockResponse>builder()
                .data(userBlockResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(PageData.getTotalElements())
                .totalPages(PageData.getTotalPages())
                .build();
    }

    public List<String> getBlockedUserIds(String userId) {
        return userBlockRepository.findBlockedUserIds(userId);
    }
}
