package com.tien.fileservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tien.fileservice.dto.response.UploadResponse;
import com.tien.fileservice.entity.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    @Mapping(target = "version", expression = "java(image.getVersion() != null ? Long.parseLong(image.getVersion()) : null)")
    UploadResponse toUploadResponse(Image image);
}

