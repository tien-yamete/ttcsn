package com.tien.interactionservice.mapper;

import com.tien.interactionservice.dto.response.LikeResponse;
import com.tien.interactionservice.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "userAvatar", ignore = true)
    LikeResponse toLikeResponse(Like like);
}

