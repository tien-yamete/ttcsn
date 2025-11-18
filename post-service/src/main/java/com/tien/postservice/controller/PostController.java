package com.tien.postservice.controller;

import com.tien.postservice.dto.ApiResponse;
import com.tien.postservice.dto.PageResponse;
import com.tien.postservice.dto.request.PostRequest;
import com.tien.postservice.dto.response.PostResponse;
import com.tien.postservice.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<PostResponse> createPost(
            @RequestParam(value = "content", required = false) String content,
            @RequestPart(value = "images", required = false) List<MultipartFile> images){
        return ApiResponse.<PostResponse>builder()
                .result(postService.createPost(content, images))
                .build();
    }

    @GetMapping("/my-posts")
    ApiResponse<PageResponse<PostResponse>> myPosts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ){
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .result(postService.getMyPosts(page, size))
                .build();
    }
}
