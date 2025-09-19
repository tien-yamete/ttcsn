package com.tien.identityservice.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tien.event.dto.NotificationEvent;
import com.tien.identityservice.constant.PredefinedRole;
import com.tien.identityservice.dto.request.UserCreationRequest;
import com.tien.identityservice.dto.request.UserUpdateRequest;
import com.tien.identityservice.dto.response.UserResponse;
import com.tien.identityservice.entity.Role;
import com.tien.identityservice.entity.User;
import com.tien.identityservice.exception.AppException;
import com.tien.identityservice.exception.ErrorCode;
import com.tien.identityservice.mapper.ProfileMapper;
import com.tien.identityservice.mapper.UserMapper;
import com.tien.identityservice.repository.RoleRepository;
import com.tien.identityservice.repository.UserRepository;
import com.tien.identityservice.repository.httpclient.ProfileClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// UserService: Nghiệp vụ quản lý người dùng:
//         - Tạo user (mã hoá mật khẩu, set role mặc định).
//         - Lấy danh sách / chi tiết user (ràng buộc phân quyền).
//         - Cập nhật / xoá user.
//         - Lấy thông tin user hiện tại từ SecurityContext.

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    RoleRepository roleRepository;

    ProfileClient profileClient;

    ProfileMapper profileMapper;

    KafkaTemplate<String, Object> kafkaTemplate;

    //    Tạo user mới.
    //            - Check trùng username.
    //            - Map DTO -> Entity.
    //            - Mã hoá mật khẩu.
    //            - Gán role mặc định USER.
    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<Role> roles = new HashSet<>();

        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);
        user.setEmailVerified(false);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        var profileRequest = profileMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(user.getId());

        var profile = profileClient.createProfile(profileRequest);

        String body = String.format("""
        <html>
            <body style="font-family: Arial, sans-serif; color: #333;">
                <h2 style="color:#4CAF50;">Welcome to Frendify 🎉</h2>
                <p>Hello <strong>%s</strong>,</p>
                <p>We’re excited to have you on board. Your journey starts here 🚀</p>
                <hr/>
                <p style="font-size: 12px; color: #888;">
                    &copy; 2025 Microservice Inc. All rights reserved.
                </p>
            </body>
        </html>
        """, request.getUsername());

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(request.getEmail())
                .subject("Welcome to microservice")
                .body(body)
                .build();

        // Publish message to kafka
        kafkaTemplate.send("notification-delivery", notificationEvent);

        var userCreationReponse = userMapper.toUserResponse(user);
        userCreationReponse.setId(profile.getResult().getId());

        return userCreationReponse;
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findById(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
}
