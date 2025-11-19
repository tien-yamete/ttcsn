package com.tien.socialservice.controller;

import com.tien.socialservice.dto.ApiResponse;
import com.tien.socialservice.dto.response.FollowResponse;
import com.tien.socialservice.service.FollowService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
}
