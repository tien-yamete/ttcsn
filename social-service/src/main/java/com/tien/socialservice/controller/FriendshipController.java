package com.tien.socialservice.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.tien.socialservice.dto.ApiResponse;
import com.tien.socialservice.dto.PageResponse;
import com.tien.socialservice.dto.response.FriendshipResponse;
import com.tien.socialservice.dto.response.ProfileResponse;
import com.tien.socialservice.service.FriendshipService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendshipController {
    FriendshipService friendshipService;

    @PostMapping("/{friendId}")
    ApiResponse<FriendshipResponse> sendFriendRequest(@PathVariable String friendId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<FriendshipResponse>builder()
                .result(friendshipService.sendFriendRequest(userId, friendId))
                .build();
    }

    @PostMapping("/{friendId}/accept")
    ApiResponse<FriendshipResponse> acceptFriendRequest(@PathVariable String friendId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<FriendshipResponse>builder()
                .result(friendshipService.acceptFriendRequest(userId, friendId))
                .build();
    }

    @PostMapping("/{friendId}/reject")
    ApiResponse<Void> rejectFriendRequest(@PathVariable String friendId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        friendshipService.rejectFriendRequest(userId, friendId);
        return ApiResponse.<Void>builder().build();
    }

    @DeleteMapping("/{friendId}")
    ApiResponse<Void> removeFriend(@PathVariable String friendId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        friendshipService.removeFriend(userId, friendId);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/friends")
    ApiResponse<PageResponse<FriendshipResponse>> getFriends(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<PageResponse<FriendshipResponse>>builder()
                .result(friendshipService.getFriends(userId, page, size))
                .build();
    }

    @GetMapping("/sent-requests")
    ApiResponse<PageResponse<FriendshipResponse>> getSentFriendRequests(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<PageResponse<FriendshipResponse>>builder()
                .result(friendshipService.getSentFriendRequests(userId, page, size))
                .build();
    }

    @GetMapping("/received-requests")
    ApiResponse<PageResponse<FriendshipResponse>> getReceivedFriendRequests(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<PageResponse<FriendshipResponse>>builder()
                .result(friendshipService.getReceivedFriendRequests(userId, page, size))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<List<ProfileResponse>> searchFriends(@RequestParam("keyword") String keyword) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<List<ProfileResponse>>builder()
                .result(friendshipService.searchFriends(userId, keyword))
                .build();
    }

    @GetMapping("/status/{friendId}")
    ApiResponse<String> getFriendshipStatus(@PathVariable String friendId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<String>builder()
                .result(friendshipService.getFriendshipStatus(userId, friendId))
                .build();
    }
}
