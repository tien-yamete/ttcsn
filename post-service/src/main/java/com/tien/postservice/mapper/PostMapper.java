package com.tien.postservice.mapper;

import com.tien.postservice.dto.response.PostResponse;
import com.tien.postservice.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
}
