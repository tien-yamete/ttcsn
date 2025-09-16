package com.tien.identityservice.mapper;

import com.tien.identityservice.dto.request.ProfileCreationRequest;
import com.tien.identityservice.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
