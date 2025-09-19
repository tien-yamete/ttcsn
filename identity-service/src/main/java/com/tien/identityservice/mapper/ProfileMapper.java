package com.tien.identityservice.mapper;

import org.mapstruct.Mapper;

import com.tien.identityservice.dto.request.ProfileCreationRequest;
import com.tien.identityservice.dto.request.UserCreationRequest;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
