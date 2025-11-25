package com.tien.postservice.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tien.postservice.configuration.FeignConfig;
import com.tien.postservice.dto.ApiResponse;
import com.tien.postservice.dto.response.UserProfileResponse;

@FeignClient(name = "profile-service", url = "${app.services.profile.url}", configuration = FeignConfig.class)
public interface ProfileClient {
    @GetMapping("/internal/users/{userId}")
    ApiResponse<UserProfileResponse> getProfile(@PathVariable String userId);
}
