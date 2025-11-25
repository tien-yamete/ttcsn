package com.tien.socialservice.mapper;

import org.mapstruct.Mapper;

import com.tien.socialservice.dto.response.UserBlockResponse;
import com.tien.socialservice.entity.UserBlock;

@Mapper(componentModel = "spring")
public interface UserBlockMapper {
    UserBlockResponse toUserBlockResponse(UserBlock userBlock);
}
