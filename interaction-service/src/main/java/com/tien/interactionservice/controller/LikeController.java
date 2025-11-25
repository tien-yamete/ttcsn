package com.tien.interactionservice.controller;

import com.tien.interactionservice.dto.ApiResponse;
import com.tien.interactionservice.dto.PageResponse;
import com.tien.interactionservice.dto.request.CreateLikeRequest;
import com.tien.interactionservice.dto.response.LikeResponse;
import com.tien.interactionservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeController {
    LikeService likeService;

    @PostMapping
    ApiResponse<LikeResponse> createLike(@Valid @RequestBody CreateLikeRequest request) {
        return ApiResponse.<LikeResponse>builder()
                .message("Like thành công")
                .result(likeService.createLike(request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> unlike(@PathVariable String id) {
        likeService.unlike(id);
        return ApiResponse.<Void>builder()
                .build();
    }

    @DeleteMapping("/post/{postId}")
    ApiResponse<Void> unlikeByPost(@PathVariable String postId) {
        likeService.unlikeByPost(postId);
        return ApiResponse.<Void>builder()
                .message("Unlike thành công")
                .build();
    }

    @DeleteMapping("/comment/{commentId}")
    ApiResponse<Void> unlikeByComment(@PathVariable String commentId) {
        likeService.unlikeByComment(commentId);
        return ApiResponse.<Void>builder()
                .message("Unlike thành công")
                .build();
    }

    @GetMapping("/post/{postId}")
    ApiResponse<PageResponse<LikeResponse>> getLikesByPost(
            @PathVariable String postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<LikeResponse>>builder()
                .message("Lấy danh sách likes thành công")
                .result(likeService.getLikesByPost(postId, page, size))
                .build();
    }
}

