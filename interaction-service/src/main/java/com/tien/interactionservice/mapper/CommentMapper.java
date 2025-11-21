package com.tien.interactionservice.mapper;

import com.tien.interactionservice.dto.response.CommentResponse;
import com.tien.interactionservice.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "userAvatar", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "replyCount", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "isLiked", ignore = true)
    CommentResponse toCommentResponse(Comment comment);
}

