package com.tien.userservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.tien.userservice.dto.ApiResponse;
import com.tien.userservice.dto.request.SearchUserRequest;
import com.tien.userservice.dto.request.UpdateProfileRequest;
import com.tien.userservice.dto.response.ProfileResponse;
import com.tien.userservice.service.ProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {

    ProfileService profileService;

    @GetMapping("/{profileId}")
    ApiResponse<ProfileResponse> getProfile(@PathVariable String profileId) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.getProfile(profileId))
                .build();
    }

    @GetMapping("/users")
    ApiResponse<List<ProfileResponse>> getAllProfiles() {
        return ApiResponse.<List<ProfileResponse>>builder()
                .result(profileService.getAllProfiles())
                .build();
    }

    @GetMapping("/users/my-profile")
    ApiResponse<ProfileResponse> getMyProfile() {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.getMyProfile())
                .build();
    }

    @PutMapping("/users/my-profile")
    ApiResponse<ProfileResponse> updateMyProfile(@RequestBody UpdateProfileRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.updateMyProfile(request))
                .build();
    }

    @PostMapping("/users/search")
    ApiResponse<List<ProfileResponse>> search(@RequestBody SearchUserRequest request) {
        return ApiResponse.<List<ProfileResponse>>builder()
                .result(profileService.search(request))
                .build();
    }
}
