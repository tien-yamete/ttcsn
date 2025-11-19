package com.tien.socialservice.service;

import com.tien.socialservice.dto.response.FollowResponse;
import com.tien.socialservice.exception.AppException;
import com.tien.socialservice.exception.ErrorCode;
import com.tien.socialservice.repository.FollowRepository;
import com.tien.socialservice.repository.FriendshipRepository;
import com.tien.socialservice.repository.UserBlockRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class FollowService {
    FollowRepository followRepository;

    UserBlockRepository userBlockRepository;

    FriendshipRepository friendshipRepository;

    public FollowResponse followUser(String followerId, String followingId) {
//        if(followerId.equals(followingId)){
//            throw new AppException(ErrorCode.CANNOT_FOLLOW_SELF);
//        }
//        if(followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)){
//            throw new AppException(ErrorCode.FOLLOW_ALREADY_EXISTS);
//        }
//        if()
        return null;
    }
}
