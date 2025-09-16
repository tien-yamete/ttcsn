package com.tien.identityservice.mapper;

import com.tien.identityservice.dto.request.PermissionRequest;
import com.tien.identityservice.dto.response.PermissionResponse;
import com.tien.identityservice.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
