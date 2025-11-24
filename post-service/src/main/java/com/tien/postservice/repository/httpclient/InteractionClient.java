package com.tien.postservice.repository.httpclient;

import com.tien.postservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "interaction-service", url = "${app.services.interaction.url}")
public interface InteractionClient {
    @GetMapping("/api/likes/post/{postId}/count")
    ApiResponse<Long> getLikeCountByPost(@PathVariable String postId);

    @GetMapping("/api/likes/post/{postId}/is-liked")
    ApiResponse<Boolean> isPostLiked(@PathVariable String postId);

    @GetMapping("/comments/post/{postId}/count")
    ApiResponse<Long> getCommentCountByPost(@PathVariable String postId);
}

