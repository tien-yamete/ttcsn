package com.tien.identityservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.tien.identityservice.dto.ApiResponse;
import com.tien.identityservice.dto.request.PermissionRequest;
import com.tien.identityservice.dto.response.PermissionResponse;
import com.tien.identityservice.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// PermissionController: Chịu trách nhiệm xử lý các API quản lý quyền (permission):
//          - POST   /permissions           : Tạo mới một quyền.
//          - GET    /permissions           : Lấy danh sách tất cả quyền hiện có.
//          - DELETE /permissions/{permission} : Xóa một quyền theo tên.

@RestController
@Slf4j
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping()
    ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable String permission) {
        permissionService.delete(permission);
        return ApiResponse.<Void>builder().build();
    }
}
