package com.tien.postservice.repository.httpclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.tien.postservice.configuration.FeignConfig;
import com.tien.postservice.dto.ApiResponse;

@FeignClient(name = "social-service", url = "${app.services.social.url}", configuration = FeignConfig.class)
public interface SocialClient {
    @GetMapping("/internal/blocks/ids")
    ApiResponse<List<String>> getBlockedUserIds();

    @GetMapping("/internal/friend-ids")
    ApiResponse<List<String>> getFriendIds();

    @GetMapping("/internal/following-ids")
    ApiResponse<List<String>> getFollowingIds();
}
