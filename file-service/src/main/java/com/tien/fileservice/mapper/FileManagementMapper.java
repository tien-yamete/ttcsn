package com.tien.fileservice.mapper;

import com.tien.fileservice.dto.FileInfo;
import com.tien.fileservice.entity.FileManagement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileManagementMapper {
    @Mapping(target = "id", source = "name")
    FileManagement toFileManagement(FileInfo fileInfo);
}
