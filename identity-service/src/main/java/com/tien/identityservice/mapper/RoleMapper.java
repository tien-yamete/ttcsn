package com.tien.identityservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tien.identityservice.dto.request.RoleRequest;
import com.tien.identityservice.dto.response.RoleResponse;
import com.tien.identityservice.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
