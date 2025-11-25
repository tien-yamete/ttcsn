package com.tien.groupservice.repository.httpclient;

import com.tien.groupservice.configuration.FeignConfig;
import com.tien.groupservice.dto.ApiResponse;
import com.tien.groupservice.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${app.services.profile.url}", configuration = FeignConfig.class)
public interface ProfileClient {
	@GetMapping("/internal/users/{userId}")
	ApiResponse<UserProfileResponse> getProfile(@PathVariable String userId);
}

