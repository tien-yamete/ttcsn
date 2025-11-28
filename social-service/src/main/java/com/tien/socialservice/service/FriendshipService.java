package com.tien.socialservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tien.socialservice.dto.PageResponse;
import com.tien.socialservice.dto.request.SearchUserRequest;
import com.tien.socialservice.dto.response.FriendshipResponse;
import com.tien.socialservice.dto.response.FriendshipStatusResponse;
import com.tien.socialservice.dto.response.ProfileResponse;
import com.tien.socialservice.dto.response.SocialCountsResponse;
import com.tien.socialservice.entity.Friendship;
import com.tien.socialservice.entity.FriendshipStatus;
import com.tien.socialservice.exception.AppException;
import com.tien.socialservice.exception.ErrorCode;
import com.tien.socialservice.mapper.FriendshipMapper;
import com.tien.socialservice.repository.FollowRepository;
import com.tien.socialservice.repository.FriendshipRepository;
import com.tien.socialservice.repository.UserBlockRepository;
import com.tien.socialservice.repository.httpclient.ProfileClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class FriendshipService {
    FriendshipRepository friendshipRepository;

    UserBlockRepository userBlockRepository;

    FriendshipMapper friendshipMapper;

    ProfileClient profileClient;

    FollowRepository followRepository;

    @Transactional
    public FriendshipResponse sendFriendRequest(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new AppException(ErrorCode.CANNOT_FRIEND_SELF);
        }
        // Kiểm tra xem đã có yêu cầu kết bạn nào tồn tại giữa hai người dùng chưa
        if (friendshipRepository.existsByUserIdAndFriendId(userId, friendId)
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
        Friendship friendship = friendshipRepository
                .findByUserIdAndFriendIdAndStatus(friendId, userId, FriendshipStatus.PENDING)
                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_PENDING));

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendship = friendshipRepository.save(friendship);

        log.info("User {} accepted friend request from user {}", userId, friendId);

        return friendshipMapper.toFriendshipResponse(friendship);
    }

    @Transactional
    public void rejectFriendRequest(String userId, String friendId) {
        Friendship friendship = friendshipRepository
                .findByUserIdAndFriendIdAndStatus(friendId, userId, FriendshipStatus.PENDING)
                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_PENDING));

        friendshipRepository.delete(friendship);

        log.info("User {} rejected friend request from user {}", userId, friendId);
    }

    @Transactional
    public void removeFriend(String userId, String friendId) {
        if (!friendshipRepository.areFriends(userId, friendId)) {
            throw new AppException(ErrorCode.FRIENDSHIP_NOT_FOUND);
        }

        friendshipRepository.deleteFriendship(userId, friendId);

        log.info("User {} removed friend {}", userId, friendId);
    }

    public PageResponse<FriendshipResponse> getFriends(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        var pageData =
                friendshipRepository.findFriendshipsByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED, pageable);

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
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

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
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

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

    public List<ProfileResponse> searchFriends(String userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEYWORD);
        }

        try {
            // Lấy danh sách blocked users
            var blockedUserIds = new HashSet<>(userBlockRepository.findBlockedUserIds(userId));
            blockedUserIds.add(userId); // Loại trừ chính user hiện tại

            // Tìm kiếm users từ profile service
            SearchUserRequest request =
                    SearchUserRequest.builder().keyword(keyword.trim()).build();

            var response = profileClient.searchUsers(request);
            var profiles = response.getResult();

            // Filter ra những người không bị block
            return profiles.stream()
                    .filter(profile -> !blockedUserIds.contains(profile.getUserId()))
                    .toList();
        } catch (Exception e) {
            log.error("Error while searching friends: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public String getFriendshipStatus(String userId, String friendId) {
        if (userId.equals(friendId)) {
            return "SELF";
        }

        Optional<Friendship> friendship1 = friendshipRepository.findByUserIdAndFriendId(userId, friendId);
        Optional<Friendship> friendship2 = friendshipRepository.findByUserIdAndFriendId(friendId, userId);

        if (friendship1.isPresent()) {
            FriendshipStatus status = friendship1.get().getStatus();
            if (status == FriendshipStatus.ACCEPTED) {
                return "ACCEPTED";
            } else if (status == FriendshipStatus.PENDING) {
                return "SENT";
            } else {
                return "REJECTED";
            }
        } else if (friendship2.isPresent()) {
            FriendshipStatus status = friendship2.get().getStatus();
            if (status == FriendshipStatus.ACCEPTED) {
                return "ACCEPTED";
            } else if (status == FriendshipStatus.PENDING) {
                return "RECEIVED";
            } else {
                return "REJECTED";
            }
        }

        return "NONE";
    }

    public List<String> getFriendIds(String userId) {
        List<Friendship> friendships = friendshipRepository.findAllFriends(userId);
        return friendships.stream()
                .map(friendship -> {
                    if (friendship.getUserId().equals(userId)) {
                        return friendship.getFriendId();
                    } else {
                        return friendship.getUserId();
                    }
                })
                .toList();
    }

    public List<ProfileResponse> getMutualFriends(String userId1, String userId2) {
        if (userId1.equals(userId2)) {
            return List.of();
        }

        try {
            // Lấy danh sách ID của bạn chung
            List<String> mutualFriendIds = friendshipRepository.findMutualFriendIds(userId1, userId2);

            if (mutualFriendIds.isEmpty()) {
                return List.of();
            }

            // Lấy thông tin profile của bạn chung
            var blockedUserIds = new HashSet<>(userBlockRepository.findBlockedUserIds(userId1));
            blockedUserIds.add(userId1); // Loại trừ chính user

            List<ProfileResponse> mutualFriends = new ArrayList<>();
            for (String friendId : mutualFriendIds) {
                if (!blockedUserIds.contains(friendId)) {
                    try {
                        var profileResponse = profileClient.getProfileByUserId(friendId);
                        if (profileResponse != null && profileResponse.getResult() != null) {
                            mutualFriends.add(profileResponse.getResult());
                        }
                    } catch (Exception e) {
                        log.warn("Failed to get profile for user {}: {}", friendId, e.getMessage());
                    }
                }
            }

            return mutualFriends;
        } catch (Exception e) {
            log.error("Error while getting mutual friends: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public List<ProfileResponse> getSuggestedFriends(String userId, int limit) {
        try {
            // Lấy danh sách bạn bè hiện tại
            List<String> friendIds = getFriendIds(userId);
            if (friendIds.isEmpty()) {
                return List.of();
            }

            // Lấy danh sách blocked users
            var blockedUserIds = new HashSet<>(userBlockRepository.findBlockedUserIds(userId));
            blockedUserIds.add(userId); // Loại trừ chính user

            // Map để đếm số bạn chung với mỗi user được gợi ý
            Map<String, Integer> mutualCountMap = new HashMap<>();

            // Với mỗi bạn bè, tìm bạn của họ
            for (String friendId : friendIds) {
                List<Friendship> friendFriendships = friendshipRepository.findAllFriends(friendId);
                for (Friendship friendship : friendFriendships) {
                    String suggestedUserId = friendship.getUserId().equals(friendId)
                            ? friendship.getFriendId()
                            : friendship.getUserId();

                    // Bỏ qua nếu:
                    // - Là chính user
                    // - Đã là bạn
                    // - Bị block
                    if (suggestedUserId.equals(userId)
                            || friendIds.contains(suggestedUserId)
                            || blockedUserIds.contains(suggestedUserId)) {
                        continue;
                    }

                    // Tăng số bạn chung
                    mutualCountMap.put(suggestedUserId, mutualCountMap.getOrDefault(suggestedUserId, 0) + 1);
                }
            }

            // Sắp xếp theo số bạn chung giảm dần và lấy top N
            List<String> suggestedUserIds = mutualCountMap.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .toList();

            if (suggestedUserIds.isEmpty()) {
                return List.of();
            }

            // Lấy thông tin profile
            List<ProfileResponse> suggestedFriends = new ArrayList<>();
            for (String suggestedUserId : suggestedUserIds) {
                try {
                    var profileResponse = profileClient.getProfileByUserId(suggestedUserId);
                    if (profileResponse != null && profileResponse.getResult() != null) {
                        suggestedFriends.add(profileResponse.getResult());
                    }
                } catch (Exception e) {
                    log.warn("Failed to get profile for user {}: {}", suggestedUserId, e.getMessage());
                }
            }

            return suggestedFriends;
        } catch (Exception e) {
            log.error("Error while getting suggested friends: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public long getPendingFriendRequestsCount(String userId) {
        return friendshipRepository.countReceivedFriendRequests(userId, FriendshipStatus.PENDING);
    }

    @Transactional
    public void cancelFriendRequest(String userId, String friendId) {
        Friendship friendship = friendshipRepository
                .findByUserIdAndFriendIdAndStatus(userId, friendId, FriendshipStatus.PENDING)
                .orElseThrow(() -> new AppException(ErrorCode.FRIEND_REQUEST_NOT_PENDING));

        friendshipRepository.delete(friendship);

        log.info("User {} cancelled friend request to user {}", userId, friendId);
    }

    public long getFriendCount(String userId) {
        return friendshipRepository.countFriends(userId);
    }

    public long getSentFriendRequestsCount(String userId) {
        return friendshipRepository.countSentFriendRequests(userId, FriendshipStatus.PENDING);
    }

    public Map<String, String> batchCheckFriendshipStatus(String userId, List<String> friendIds) {
        Map<String, String> statusMap = new HashMap<>();
        for (String friendId : friendIds) {
            statusMap.put(friendId, getFriendshipStatus(userId, friendId));
        }
        return statusMap;
    }

    public SocialCountsResponse getAllSocialCounts(String userId) {
        long friendsCount = getFriendCount(userId);
        long followersCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        long pendingFriendRequestsCount = getPendingFriendRequestsCount(userId);
        long sentFriendRequestsCount = getSentFriendRequestsCount(userId);
        long blockedUsersCount = userBlockRepository.countByBlockerId(userId);

        return SocialCountsResponse.builder()
                .friendsCount(friendsCount)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .pendingFriendRequestsCount(pendingFriendRequestsCount)
                .sentFriendRequestsCount(sentFriendRequestsCount)
                .blockedUsersCount(blockedUsersCount)
                .build();
    }
}
