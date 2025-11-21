package com.tien.profileservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.tien.profileservice.dto.request.ProfileCreationRequest;
import com.tien.profileservice.dto.request.UpdateProfileRequest;
import com.tien.profileservice.dto.response.ProfileResponse;
import com.tien.profileservice.entity.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    Profile toProfile(ProfileCreationRequest request); // chuyen du lieu tu dto thanh entity de luu vao db

    ProfileResponse toProfileResponse(Profile entity); // chuyen du lieu tu entity sang dto de tra ve client

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Profile entity, UpdateProfileRequest request);
}
