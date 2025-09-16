package com.tien.userservice.mapper;

import com.tien.userservice.dto.request.ProfileCreationRequest;
import com.tien.userservice.dto.request.UpdateProfileRequest;
import com.tien.userservice.dto.response.ProfileResponse;
import com.tien.userservice.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    Profile toProfile(ProfileCreationRequest request); // chuyen du lieu tu dto thanh entity de luu vao db

    ProfileResponse toProfileResponse(Profile entity); // chuyen du lieu tu entity sang dto de tra ve client

    void update(@MappingTarget Profile entity, UpdateProfileRequest request);
}
