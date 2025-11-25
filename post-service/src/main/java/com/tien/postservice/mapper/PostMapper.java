package com.tien.postservice.mapper;

import org.mapstruct.Mapper;

import com.tien.postservice.dto.response.PostResponse;
import com.tien.postservice.entity.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
}
