package com.tien.socialservice.mapper;

import com.tien.socialservice.dto.response.UserBlockResponse;
import com.tien.socialservice.entity.UserBlock;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserBlockMapper {
    UserBlockResponse toUserBlockResponse(UserBlock userBlock);
}
