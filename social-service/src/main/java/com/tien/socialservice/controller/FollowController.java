package com.tien.socialservice.controller;

import com.tien.socialservice.dto.ApiResponse;
import com.tien.socialservice.dto.PageResponse;
import com.tien.socialservice.dto.response.FollowResponse;
import com.tien.socialservice.dto.response.UserSocialInfoResponse;
import com.tien.socialservice.service.FollowService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowController {
    FollowService followService;

    @PostMapping("/{followingId}")
    ApiResponse<FollowResponse> followUser(
            @PathVariable String followingId) {
        String followerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<FollowResponse>builder()
                .result(followService.followUser(followerId, followingId))
                .build();
    }

    @DeleteMapping("/{followingId}")
    ApiResponse<Void> unfollowUser(
            @PathVariable String followingId) {
        String followerId = SecurityContextHolder.getContext().getAuthentication().getName();
        followService.unfollowUser(followerId, followingId);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/following/{userId}")
    ApiResponse<PageResponse<FollowResponse>> getFollowingUser(
            @PathVariable String userId,
            @RequestParam(value ="page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false,defaultValue = "20") int size) {
        return ApiResponse.<PageResponse<FollowResponse>>builder()
                .result(followService.getFollowingUser(userId, page, size))
                .build();
    }

    @GetMapping("/followers/{userId}")
    ApiResponse<PageResponse<FollowResponse>> getFollowerUser(
            @PathVariable String userId,
            @RequestParam(value ="page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false,defaultValue = "20") int size) {
        return ApiResponse.<PageResponse<FollowResponse>>builder()
                .result(followService.getFollowerUser(userId, page, size))
                .build();
    }

    @GetMapping("/info/{userId}")
    ApiResponse<UserSocialInfoResponse> getUserSocialInfo(
            @PathVariable String userId) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<UserSocialInfoResponse>builder()
                .result(followService.getUserSocialInfo(currentUserId, userId))
                .build();
    }

    @GetMapping("/check/{followingId}")
    ApiResponse<Boolean> checkFollowing(@PathVariable String followingId) {
        String followerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<Boolean>builder()
                .result(followService.checkFollowing(followerId, followingId))
                .build();
    }

    @GetMapping("/internal/following-ids")
    ApiResponse<List<String>> getFollowingIds() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<List<String>>builder()
                .result(followService.getFollowingIds(userId))
                .build();
    }
}
