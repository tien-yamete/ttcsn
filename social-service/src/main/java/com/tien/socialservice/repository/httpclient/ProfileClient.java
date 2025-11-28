package com.tien.socialservice.repository.httpclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tien.socialservice.configuration.FeignConfig;
import com.tien.socialservice.dto.ApiResponse;
import com.tien.socialservice.dto.request.SearchUserRequest;
import com.tien.socialservice.dto.response.ProfileResponse;

@FeignClient(name = "profile-service", url = "${app.services.profile.url}", configuration = FeignConfig.class)
public interface ProfileClient {
    @PostMapping("/users/search")
    ApiResponse<List<ProfileResponse>> searchUsers(@RequestBody SearchUserRequest request);

    @org.springframework.web.bind.annotation.GetMapping("/internal/users/{userId}")
    ApiResponse<ProfileResponse> getProfileByUserId(@org.springframework.web.bind.annotation.PathVariable String userId);
}
