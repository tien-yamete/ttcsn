package com.tien.interactionservice.repository.httpclient;

import com.tien.interactionservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "post-service",
        url = "${app.services.post.url}")
public interface PostClient {
    @GetMapping("/internal/posts/{postId}/exists")
    ApiResponse<Boolean> checkPostExists(@PathVariable String postId);
}

