package com.tien.identity_service.service;

import com.tien.identity_service.dto.request.RoleRequest;
import com.tien.identity_service.dto.response.RoleResponse;
import com.tien.identity_service.mapper.RoleMapper;
import com.tien.identity_service.repository.PermissionRepository;
import com.tien.identity_service.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

// RoleService: Xử lý nghiệp vụ liên quan đến Role:
//          - Tạo role mới và gán danh sách permission.
//          - Lấy danh sách toàn bộ role.
//          - Xóa role theo tên.

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;

    PermissionRepository permissionRepository;

    RoleMapper roleMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String roleName) {
        roleRepository.deleteById(roleName);
    }
}
