package com.tien.interactionservice.controller;

import com.tien.interactionservice.dto.ApiResponse;
import com.tien.interactionservice.service.CommentService;
import com.tien.interactionservice.service.LikeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/internal")
public class InternalInteractionController {
    CommentService commentService;
    LikeService likeService;

    @GetMapping("/likes/post/{postId}/count")
    ApiResponse<Long> getLikeCountByPost(@PathVariable String postId) {
        return ApiResponse.<Long>builder()
                .result(likeService.getLikeCountByPost(postId))
                .build();
    }

    @GetMapping("/likes/post/{postId}/is-liked")
    ApiResponse<Boolean> isPostLiked(@PathVariable String postId) {
        return ApiResponse.<Boolean>builder()
                .result(likeService.isPostLiked(postId))
                .build();
    }

    @GetMapping("/comments/post/{postId}/count")
    ApiResponse<Long> getCommentCountByPost(@PathVariable String postId) {
        return ApiResponse.<Long>builder()
                .result(commentService.getCommentCountByPost(postId))
                .build();
    }
}

