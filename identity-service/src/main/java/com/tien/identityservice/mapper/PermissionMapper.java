package com.tien.identityservice.mapper;

import org.mapstruct.Mapper;

import com.tien.identityservice.dto.request.PermissionRequest;
import com.tien.identityservice.dto.response.PermissionResponse;
import com.tien.identityservice.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
