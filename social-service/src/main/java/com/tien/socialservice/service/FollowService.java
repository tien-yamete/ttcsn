package com.tien.socialservice.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tien.socialservice.dto.PageResponse;
import com.tien.socialservice.dto.response.FollowResponse;
import com.tien.socialservice.dto.response.UserSocialInfoResponse;
import com.tien.socialservice.entity.Follow;
import com.tien.socialservice.exception.AppException;
import com.tien.socialservice.exception.ErrorCode;
import com.tien.socialservice.mapper.FollowMapper;
import com.tien.socialservice.repository.FollowRepository;
import com.tien.socialservice.repository.FriendshipRepository;
import com.tien.socialservice.repository.UserBlockRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class FollowService {
    FollowRepository followRepository;

    UserBlockRepository userBlockRepository;

    FriendshipRepository friendshipRepository;

    FollowMapper followMapper;

    @Transactional
    public FollowResponse followUser(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
            throw new AppException(ErrorCode.CANNOT_FOLLOW_SELF);
        }
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new AppException(ErrorCode.FOLLOW_ALREADY_EXISTS);
        }

        // check block
        if (userBlockRepository.isBlocked(followerId, followingId)) {
            throw new AppException(ErrorCode.USER_ALREADY_BLOCKED);
        }

        Follow follow =
                Follow.builder().followerId(followerId).followingId(followingId).build();

        follow = followRepository.save(follow);
        log.info("User {} followed user {}", followerId, followingId);

        return followMapper.toFollowResponse(follow);
    }

    @Transactional
    public void unfollowUser(String followerId, String followingId) {
        Follow follow = followRepository
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new AppException(ErrorCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);
        log.info("User {} unfollowed user {}", followerId, followingId);
    }

    public PageResponse<FollowResponse> getFollowingUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        var pageData = followRepository.findFollowingByUserId(userId, pageable);

        var followResponses = pageData.getContent().stream()
                .map(followMapper::toFollowResponse)
                .toList();

        return PageResponse.<FollowResponse>builder()
                .data(followResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .build();
    }

    public PageResponse<FollowResponse> getFollowerUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        var pageData = followRepository.findFollowersByUserId(userId, pageable);

        var followResponses = pageData.getContent().stream()
                .map(followMapper::toFollowResponse)
                .toList();

        return PageResponse.<FollowResponse>builder()
                .data(followResponses)
                .currentPage(page)
                .pageSize(size)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .build();
    }

    public UserSocialInfoResponse getUserSocialInfo(String currentUserId, String userId) {
        long followingCount = followRepository.countByFollowerId(userId);
        long followerCount = followRepository.countByFollowingId(userId);
        long friendCount = friendshipRepository.findAllFriends(userId).size();

        boolean isFollowing = false;
        boolean isFriend = false;
        boolean isBlocked = false;

        if (currentUserId != null && !currentUserId.equals(userId)) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUserId, userId);
            isFriend = friendshipRepository.areFriends(currentUserId, userId);
            isBlocked = userBlockRepository.isBlocked(currentUserId, userId);
        }

        return UserSocialInfoResponse.builder()
                .userId(userId)
                .followingCount(followingCount)
                .followersCount(followerCount)
                .friendsCount(friendCount)
                .isFollowing(isFollowing)
                .isFriend(isFriend)
                .isBlocked(isBlocked)
                .build();
    }

    public boolean checkFollowing(String followerId, String followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public List<String> getFollowingIds(String userId) {
        return followRepository.findFollowingIdsByUserId(userId);
    }
}
