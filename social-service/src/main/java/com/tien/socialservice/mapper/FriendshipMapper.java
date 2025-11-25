package com.tien.socialservice.mapper;

import org.mapstruct.Mapper;

import com.tien.socialservice.dto.response.FriendshipResponse;
import com.tien.socialservice.entity.Friendship;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {
    FriendshipResponse toFriendshipResponse(Friendship friendship);
}
