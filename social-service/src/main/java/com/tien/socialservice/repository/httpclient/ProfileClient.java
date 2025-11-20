package com.tien.socialservice.repository.httpclient;

import com.tien.socialservice.dto.ApiResponse;
import com.tien.socialservice.dto.request.SearchUserRequest;
import com.tien.socialservice.dto.response.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "profile-service", url = "${app.services.profile.url}")
public interface ProfileClient {
    @PostMapping("/users/search")
    ApiResponse<List<ProfileResponse>> searchUsers(@RequestBody SearchUserRequest request);
}

