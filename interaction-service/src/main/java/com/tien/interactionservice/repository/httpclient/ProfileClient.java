package com.tien.interactionservice.repository.httpclient;

import com.tien.interactionservice.configuration.FeignConfig;
import com.tien.interactionservice.dto.ApiResponse;
import com.tien.interactionservice.dto.response.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "profile-service",
        url = "${app.services.profile.url}",
        configuration = FeignConfig.class)
public interface ProfileClient {
    @GetMapping("/internal/users/{userId}")
    ApiResponse<ProfileResponse> getProfile(@PathVariable String userId);

    @GetMapping("/internal/users/batch")
    ApiResponse<List<ProfileResponse>> getProfiles(@RequestParam("userIds") List<String> userIds);
}

