package com.tien.profileservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.tien.profileservice.dto.ApiResponse;
import com.tien.profileservice.dto.request.SearchUserRequest;
import com.tien.profileservice.dto.request.UpdateProfileRequest;
import com.tien.profileservice.dto.response.ProfileResponse;
import com.tien.profileservice.service.ProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/users")
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

    @GetMapping
    ApiResponse<List<ProfileResponse>> getAllProfiles() {
        return ApiResponse.<List<ProfileResponse>>builder()
                .result(profileService.getAllProfiles())
                .build();
    }

    @GetMapping("/my-profile")
    ApiResponse<ProfileResponse> getMyProfile() {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.getMyProfile())
                .build();
    }

    @PutMapping("/my-profile")
    ApiResponse<ProfileResponse> updateMyProfile(@RequestBody UpdateProfileRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.updateMyProfile(request))
                .build();
    }

    @PostMapping("/search")
    ApiResponse<List<ProfileResponse>> search(@RequestBody SearchUserRequest request) {
        return ApiResponse.<List<ProfileResponse>>builder()
                .result(profileService.search(request))
                .build();
    }

    @PutMapping("/avatar")
    ApiResponse<ProfileResponse> updateAvatar(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.updateAvatar(file))
                .build();
    }

    @PutMapping("/background")
    ApiResponse<ProfileResponse> updateBackgroundImage(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.uploadBackgroundImage(file))
                .build();
    }
}
