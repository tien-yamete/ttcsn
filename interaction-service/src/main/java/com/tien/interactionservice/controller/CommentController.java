package com.tien.interactionservice.controller;

import com.tien.interactionservice.dto.ApiResponse;
import com.tien.interactionservice.dto.PageResponse;
import com.tien.interactionservice.dto.request.CreateCommentRequest;
import com.tien.interactionservice.dto.request.UpdateCommentRequest;
import com.tien.interactionservice.dto.response.CommentResponse;
import com.tien.interactionservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping
    ApiResponse<CommentResponse> createComment(@Valid @RequestBody CreateCommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .message("Tạo comment thành công")
                .result(commentService.createComment(request))
                .build();
    }

    @GetMapping("/post/{postId}")
    ApiResponse<PageResponse<CommentResponse>> getCommentsByPost(
            @PathVariable String postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .message("Lấy danh sách comments thành công")
                .result(commentService.getCommentsByPost(postId, page, size))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<CommentResponse> updateComment(
            @PathVariable String id,
            @Valid @RequestBody UpdateCommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .message("Cập nhật comment thành công")
                .result(commentService.updateComment(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ApiResponse.<Void>builder()
                .message("Xóa comment thành công")
                .build();
    }

    @GetMapping("/post/{postId}/count")
    ApiResponse<Long> getCommentCountByPost(@PathVariable String postId) {
        return ApiResponse.<Long>builder()
                .message("Lấy số lượng comments thành công")
                .result(commentService.getCommentCountByPost(postId))
                .build();
    }
}

