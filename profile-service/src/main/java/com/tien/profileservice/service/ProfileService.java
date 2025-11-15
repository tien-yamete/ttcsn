package com.tien.profileservice.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tien.profileservice.dto.request.ProfileCreationRequest;
import com.tien.profileservice.dto.request.SearchUserRequest;
import com.tien.profileservice.dto.request.UpdateProfileRequest;
import com.tien.profileservice.dto.response.ProfileResponse;
import com.tien.profileservice.entity.Profile;
import com.tien.profileservice.exception.AppException;
import com.tien.profileservice.exception.ErrorCode;
import com.tien.profileservice.mapper.ProfileMapper;
import com.tien.profileservice.repository.ProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileService {

    ProfileRepository profileRepository;

    ProfileMapper profileMapper;

    ImageUploadKafkaService imageUploadKafkaService;
    public ProfileResponse createProfile(ProfileCreationRequest request) {
        Profile userProfile = profileMapper.toProfile(request);
        userProfile = profileRepository.save(userProfile);

        return profileMapper.toProfileResponse(userProfile);
    }

    public ProfileResponse getProfile(String userId) {
        Profile userProfile =
                profileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return profileMapper.toProfileResponse(userProfile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ProfileResponse> getAllProfiles() {
        var profiles = profileRepository.findAll();

        return profiles.stream().map(profileMapper::toProfileResponse).toList();
    }

    public ProfileResponse getMyProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        log.info(userId);

        var profile =
                profileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return profileMapper.toProfileResponse(profile);
    }

    public ProfileResponse updateMyProfile(UpdateProfileRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        var profile =
                profileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        profileMapper.update(profile, request);

        return profileMapper.toProfileResponse(profileRepository.save(profile));
    }

    public ProfileResponse updateAvatar(MultipartFile file) {
        // 1) Validate auth
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        String userId = authentication.getName();

        var profile =
                profileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Upload file using Kafka
        try {
            var uploadedEvent = imageUploadKafkaService.uploadAvatar(file, userId);
            profile.setAvatar(uploadedEvent.imageUrl());
        } catch (Exception e) {
            log.error("Failed to upload avatar via Kafka: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return profileMapper.toProfileResponse(profileRepository.save(profile));
    }

    public List<ProfileResponse> search(SearchUserRequest request) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Profile> userProfiles = profileRepository.findByUsernameContainingIgnoreCase(request.getKeyword());
        log.info(userId);
        log.info(request.getKeyword());
        log.info(userProfiles.toString());
        return userProfiles.stream()
                .filter(userProfile -> !userId.equals(userProfile.getUserId()))
                .map(profileMapper::toProfileResponse)
                .toList();
    }
}
