package com.tien.chatservice.repository.httpclient;

import com.tien.chatservice.dto.ApiResponse;
import com.tien.chatservice.dto.response.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${app.services.profile.url}")
public interface ProfileClient {
    @GetMapping("/internal/users/{userId}")
    ApiResponse<ProfileResponse> getProfile(@PathVariable String userId);
}
