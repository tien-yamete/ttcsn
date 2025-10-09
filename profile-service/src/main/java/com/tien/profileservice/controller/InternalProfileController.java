package com.tien.profileservice.controller;

import org.springframework.web.bind.annotation.*;

import com.tien.profileservice.dto.ApiResponse;
import com.tien.profileservice.dto.request.ProfileCreationRequest;
import com.tien.profileservice.dto.response.ProfileResponse;
import com.tien.profileservice.service.ProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalProfileController {
    ProfileService profileService;

    @PostMapping("/internal/users")
    ApiResponse<ProfileResponse> createProfile(@RequestBody ProfileCreationRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.createProfile(request))
                .build();
    }

    @GetMapping("/internal/users/{userId}")
    ApiResponse<ProfileResponse> getProfile(@PathVariable String userId) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.getProfile(userId))
                .build();
    }
}
