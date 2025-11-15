package com.tien.fileservice.mapper;

import org.mapstruct.Mapper;

import com.tien.fileservice.dto.response.UploadResponse;
import com.tien.fileservice.entity.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    UploadResponse toUploadResponse(Image image);
}
