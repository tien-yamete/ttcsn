package com.tien.identityservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.tien.identityservice.dto.ApiResponse;
import com.tien.identityservice.dto.request.RoleRequest;
import com.tien.identityservice.dto.response.RoleResponse;
import com.tien.identityservice.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * RoleController: Controller xử lý các API quản lý vai trò (role).
 * - POST   /roles          : Tạo mới một role (chỉ ADMIN)
 * - GET    /roles          : Lấy danh sách tất cả role hiện có (chỉ ADMIN)
 * - DELETE /roles/{role}   : Xóa một role theo tên (chỉ ADMIN)
 */
@RestController
@Slf4j
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable String role) {
        roleService.delete(role);
        return ApiResponse.<Void>builder().build();
    }
}
