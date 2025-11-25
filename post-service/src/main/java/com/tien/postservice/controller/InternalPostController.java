package com.tien.postservice.controller;

import org.springframework.web.bind.annotation.*;

import com.tien.postservice.dto.ApiResponse;
import com.tien.postservice.service.PostService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/internal")
public class InternalPostController {
    PostService postService;

    @GetMapping("/posts/{postId}/exists")
    ApiResponse<Boolean> checkPostExists(@PathVariable String postId) {
        boolean exists = postService.checkPostExists(postId);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message("Check post exists")
                .result(exists)
                .build();
    }
}
