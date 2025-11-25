package com.tien.socialservice.mapper;

import org.mapstruct.Mapper;

import com.tien.socialservice.dto.response.FollowResponse;
import com.tien.socialservice.entity.Follow;

@Mapper(componentModel = "spring")
public interface FollowMapper {
    FollowResponse toFollowResponse(Follow follow);
}
