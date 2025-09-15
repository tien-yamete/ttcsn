package com.tien.identity_service.service;

import com.tien.identity_service.dto.request.PermissionRequest;
import com.tien.identity_service.dto.response.PermissionResponse;
import com.tien.identity_service.entity.Permission;
import com.tien.identity_service.mapper.PermissionMapper;
import com.tien.identity_service.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

// PermissionService: Chịu trách nhiệm xử lý nghiệp vụ liên quan đến Permission:
//          - Tạo permission mới từ request.
//          - Lấy toàn bộ danh sách permission.
//          - Xóa permission theo tên.

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;

    PermissionMapper permissionMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String permissionName) {
        permissionRepository.deleteById(permissionName);
    }
}
