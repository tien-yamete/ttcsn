package com.tien.socialservice.mapper;

import com.tien.socialservice.dto.response.FollowResponse;
import com.tien.socialservice.entity.Follow;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FollowMapper {
    FollowResponse toFollowResponse(Follow follow);
}
