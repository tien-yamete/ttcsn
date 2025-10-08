package com.tien.identityservice.controller;

import java.util.List;

import com.tien.identityservice.dto.request.*;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.tien.identityservice.dto.ApiResponse;
import com.tien.identityservice.dto.response.UserResponse;
import com.tien.identityservice.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// UserController:Chịu trách nhiệm xử lý các API quản lý người dùng trong hệ thống:
//          - POST   /users              : Tạo mới một user.
//          - GET    /users              : Lấy danh sách tất cả user.
//          - GET    /users/{userId}     : Lấy thông tin chi tiết của user theo ID.
//          - GET    /users/myInfo       : Lấy thông tin user hiện đang đăng nhập.
//          - PUT    /users/{userId}     : Cập nhật thông tin của user theo ID.
//          - DELETE /users/{userId}     : Xóa user theo ID.

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
}
