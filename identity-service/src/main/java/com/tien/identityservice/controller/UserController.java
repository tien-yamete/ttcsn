package com.tien.identityservice.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.tien.identityservice.dto.ApiResponse;
import com.tien.identityservice.dto.request.ChangePasswordRequest;
import com.tien.identityservice.dto.request.UserUpdateRequest;
import com.tien.identityservice.dto.response.UserResponse;
import com.tien.identityservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * UserController: Controller xử lý các API quản lý người dùng trong hệ thống.
 * - GET    /users              : Lấy danh sách tất cả user (chỉ ADMIN)
 * - GET    /users/{userId}     : Lấy thông tin chi tiết của user theo ID (chỉ ADMIN)
 * - GET    /users/myInfo       : Lấy thông tin user hiện đang đăng nhập
 * - PUT    /users/{userId}     : Cập nhật thông tin của user theo ID (chỉ ADMIN)
 * - DELETE /users/{userId}     : Xóa user theo ID (chỉ ADMIN)
 * - PUT    /users/change-password : Đổi mật khẩu cho user hiện tại
 */
@Slf4j
@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {

    UserService userService;

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }

    @PutMapping("/change-password")
    ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<Void>builder()
                .message("Đổi mật khẩu thành công")
                .build();
    }
}
