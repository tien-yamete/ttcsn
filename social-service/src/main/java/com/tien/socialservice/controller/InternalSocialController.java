package com.tien.socialservice.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tien.socialservice.dto.ApiResponse;
import com.tien.socialservice.service.FollowService;
import com.tien.socialservice.service.FriendshipService;
import com.tien.socialservice.service.UserBlockService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/internal")
public class InternalSocialController {
    FriendshipService friendshipService;
    FollowService followService;
    UserBlockService userBlockService;

    @GetMapping("/friend-ids")
    ApiResponse<List<String>> getFriendIds() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<List<String>>builder()
                .result(friendshipService.getFriendIds(userId))
                .build();
    }

    @GetMapping("/following-ids")
    ApiResponse<List<String>> getFollowingIds() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<List<String>>builder()
                .result(followService.getFollowingIds(userId))
                .build();
    }

    @GetMapping("/blocks/ids")
    ApiResponse<List<String>> getBlockedUserIds() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<List<String>>builder()
                .result(userBlockService.getBlockedUserIds(userId))
                .build();
    }
}
