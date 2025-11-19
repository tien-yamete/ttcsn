package com.tien.socialservice.mapper;

import com.tien.socialservice.dto.response.FriendshipResponse;
import com.tien.socialservice.entity.Friendship;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {
    FriendshipResponse toFriendshipResponse(Friendship friendship);
}
