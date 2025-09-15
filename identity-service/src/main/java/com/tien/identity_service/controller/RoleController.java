package com.tien.identity_service.controller;

import com.tien.identity_service.dto.ApiResponse;
import com.tien.identity_service.dto.request.RoleRequest;
import com.tien.identity_service.dto.response.RoleResponse;
import com.tien.identity_service.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// RoleController: Chịu trách nhiệm xử lý các API quản lý vai trò (role):
//          - POST   /roles          : Tạo mới một role.
//          - GET    /roles          : Lấy danh sách tất cả role hiện có.
//          - DELETE /roles/{role}   : Xóa một role theo tên.

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
