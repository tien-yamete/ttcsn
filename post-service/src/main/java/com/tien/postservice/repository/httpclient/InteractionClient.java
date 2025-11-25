package com.tien.postservice.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tien.postservice.configuration.FeignConfig;
import com.tien.postservice.dto.ApiResponse;

@FeignClient(name = "interaction-service", url = "${app.services.interaction.url}", configuration = FeignConfig.class)
public interface InteractionClient {
    @GetMapping("/internal/likes/post/{postId}/count")
    ApiResponse<Long> getLikeCountByPost(@PathVariable String postId);

    @GetMapping("/internal/likes/post/{postId}/is-liked")
    ApiResponse<Boolean> isPostLiked(@PathVariable String postId);

    @GetMapping("/internal/comments/post/{postId}/count")
    ApiResponse<Long> getCommentCountByPost(@PathVariable String postId);
}
