package com.tien.fileservice.service;

import com.tien.fileservice.dto.response.FileData;
import com.tien.fileservice.dto.response.FileResponse;
import com.tien.fileservice.exception.AppException;
import com.tien.fileservice.exception.ErrorCode;
import com.tien.fileservice.mapper.FileManagementMapper;
import com.tien.fileservice.repository.FileManagementRepository;
import com.tien.fileservice.repository.FileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileService {
    FileRepository fileRepository;
    FileManagementRepository fileManagementRepository;
    FileManagementMapper fileManagementMapper;
    public FileResponse uploadFile(MultipartFile file) throws IOException {
        //Store file to file system
        var fileInfo = fileRepository.store(file);
        //Create file management info
        var fileManagement = fileManagementMapper.toFileManagement(fileInfo);

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        fileManagement.setOwnerId(userId);

        fileManagement =  fileManagementRepository.save(fileManagement);

        return FileResponse.builder()
                .url(fileInfo.getUrl())
                .originalFileName(file.getOriginalFilename())
                .build();
    }

    public FileData downloadFile(String fileName) throws IOException {
        var fileManagement = fileManagementRepository.findById(fileName).orElseThrow(
                () -> new AppException(ErrorCode.FILE_NOT_FOUND)
        );

        var resource = fileRepository.read(fileManagement);
        return new FileData(fileManagement.getContentType(), resource);
    }
}
