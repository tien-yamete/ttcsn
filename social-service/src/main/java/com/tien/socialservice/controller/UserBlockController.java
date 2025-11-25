package com.tien.socialservice.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tien.socialservice.dto.ApiResponse;
import com.tien.socialservice.dto.PageResponse;
import com.tien.socialservice.dto.response.UserBlockResponse;
import com.tien.socialservice.service.UserBlockService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/blocks")
public class UserBlockController {
    UserBlockService userBlockService;

    @PostMapping("/{blockedId}")
    ApiResponse<UserBlockResponse> blockUser(@PathVariable String blockedId) {
        String blockerId =
                SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<UserBlockResponse>builder()
                .result(userBlockService.blockUser(blockerId, blockedId))
                .build();
    }

    @DeleteMapping("/{blockedId}")
    ApiResponse<Void> unblockUser(@PathVariable String blockedId) {
        String blockerId =
                SecurityContextHolder.getContext().getAuthentication().getName();
        userBlockService.unblockUser(blockerId, blockedId);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping
    ApiResponse<PageResponse<UserBlockResponse>> getBlockedUsers(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<PageResponse<UserBlockResponse>>builder()
                .result(userBlockService.getBlockedUsers(userId, page, size))
                .build();
    }
}
