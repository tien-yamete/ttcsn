package com.tien.identityservice.service;

import org.springframework.stereotype.Service;

import com.tien.identityservice.dto.ApiResponse;
import com.tien.identityservice.dto.request.ProfileCreationRequest;
import com.tien.identityservice.dto.request.UserCreationRequest;
import com.tien.identityservice.dto.response.UserProfileResponse;
import com.tien.identityservice.exception.AppException;
import com.tien.identityservice.exception.ErrorCode;
import com.tien.identityservice.mapper.ProfileMapper;
import com.tien.identityservice.repository.httpclient.ProfileClient;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// ProfileService: Service giao tiếp với profile-service để quản lý profile của user.

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileService {
    ProfileClient profileClient;

    ProfileMapper profileMapper;

    // Tạo profile mới bằng cách gọi profile-service qua Feign Client
    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        try {
            ApiResponse<UserProfileResponse> response = profileClient.createProfile(request);
            if (response == null || response.getResult() == null) {
                log.error("Profile service trả về null response cho user: {}", request.getUserId());
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
            return response.getResult();
        } catch (FeignException e) {
            log.error("Lỗi khi gọi profile-service để tạo profile cho user: {}", request.getUserId(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    // Tạo profile từ UserCreationRequest (khi đăng ký)
    public UserProfileResponse createProfileFromCreation(UserCreationRequest request, String userId) {
        ProfileCreationRequest profileRequest = profileMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(userId);
        return createProfile(profileRequest);
    }
}
