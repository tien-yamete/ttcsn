package com.tien.socialservice.service;

import com.tien.socialservice.dto.PageResponse;
import com.tien.socialservice.dto.response.FriendshipResponse;
import com.tien.socialservice.entity.Friendship;
import com.tien.socialservice.entity.FriendshipStatus;
import com.tien.socialservice.exception.AppException;
import com.tien.socialservice.exception.ErrorCode;
import com.tien.socialservice.mapper.FriendshipMapper;
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

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class FriendshipService {
    FriendshipRepository friendshipRepository;

    UserBlockRepository userBlockRepository;

    FriendshipMapper friendshipMapper;

    @Transactional
    public FriendshipResponse sendFriendRequest(String userId, String friendId) {
        if(userId.equals(friendId)) {
            throw new AppException(ErrorCode.CANNOT_FRIEND_SELF);
        }
        // Kiểm tra xem đã có yêu cầu kết bạn nào tồn tại giữa hai người dùng chưa
        if(friendshipRepository.existsByUserIdAndFriendId(userId, friendId)
        || friendshipRepository.existsByUserIdAndFriendId(friendId, userId)) {
            throw new AppException(ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
        }

        if (userBlockRepository.isBlocked(userId, friendId) || userBlockRepository.isBlocked(friendId, userId)) {
            throw new AppException(ErrorCode.USER_ALREADY_BLOCKED);
        }

        Friendship friendship = Friendship.builder()
                .userId(userId)
                .friendId(friendId)
                .status(FriendshipStatus.PENDING)
                .build();

        friendship = friendshipRepository.save(friendship);

        log.info("User {} sent friend request to user {}", userId, friendId);

        return friendshipMapper.toFriendshipResponse(friendship);
    }

    @Transactional
    public FriendshipResponse acceptFriendRequest(String userId, String friendId) {
        Friendship friendship = friendshipRepository.findByUserIdAndFriendIdAndStatus(friendId, userId, FriendshipStatus.PENDING)
                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_PENDING));

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship = friendshipRepository.save(friendship);

        log.info("User {} accepted friend request from user {}", userId, friendId);

        return friendshipMapper.toFriendshipResponse(friendship);
    }

    @Transactional
    public void rejectFriendRequest(String userId, String friendId) {
        Friendship friendship = friendshipRepository.findByUserIdAndFriendIdAndStatus(friendId, userId, FriendshipStatus.PENDING)
                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_PENDING));

        friendshipRepository.delete(friendship);

        log.info("User {} rejected friend request from user {}", userId, friendId);
    }

    @Transactional
    public void removeFriend(String userId, String friendId) {
        if(!friendshipRepository.areFriends(userId, friendId)) {
            throw new AppException(ErrorCode.FRIENDSHIP_NOT_FOUND);
        }

        friendshipRepository.deleteFriendship(userId ,friendId);

        log.info("User {} removed friend {}", userId, friendId);
    }

    public PageResponse<FriendshipResponse> getFriends(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());

        var pageData = friendshipRepository.findFriendshipsByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED, pageable);

        var friendshipResponses = pageData.map(friendshipMapper::toFriendshipResponse);

        return PageResponse.<FriendshipResponse>builder()
                .currentPage(page)
                .totalPages(pageData.getTotalPages())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .data(friendshipResponses.getContent())
                .build();
    }

    public PageResponse<FriendshipResponse> getSentFriendRequests(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());

        var pageData = friendshipRepository.findSentFriendRequests(userId, FriendshipStatus.PENDING, pageable);

        var sentFriendshipResponses = pageData.map(friendshipMapper::toFriendshipResponse);

        return PageResponse.<FriendshipResponse>builder()
                .currentPage(page)
                .totalPages(pageData.getTotalPages())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .data(sentFriendshipResponses.getContent())
                .build();
    }

    public PageResponse<FriendshipResponse> getReceivedFriendRequests(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());

        var pageData = friendshipRepository.findReceivedFriendRequests(userId, FriendshipStatus.PENDING, pageable);

        var receivedFriendshipResponses = pageData.map(friendshipMapper::toFriendshipResponse);

        return PageResponse.<FriendshipResponse>builder()
                .currentPage(page)
                .totalPages(pageData.getTotalPages())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .data(receivedFriendshipResponses.getContent())
                .build();
    }
}
