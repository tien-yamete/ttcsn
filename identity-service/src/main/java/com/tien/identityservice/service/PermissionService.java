package com.tien.identityservice.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.tien.identityservice.dto.request.PermissionRequest;
import com.tien.identityservice.dto.response.PermissionResponse;
import com.tien.identityservice.entity.Permission;
import com.tien.identityservice.mapper.PermissionMapper;
import com.tien.identityservice.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// PermissionService: Service xử lý nghiệp vụ liên quan đến Permission.
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;

    PermissionMapper permissionMapper;

    // Tạo permission mới từ request (chỉ ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    // Lấy toàn bộ danh sách permission (chỉ ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    // Xóa permission theo tên (chỉ ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String permissionName) {
        permissionRepository.deleteById(permissionName);
    }
}
