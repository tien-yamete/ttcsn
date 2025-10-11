package com.tien.fileservice.controller;

import com.tien.fileservice.dto.ApiResponse;
import com.tien.fileservice.dto.response.FileResponse;
import com.tien.fileservice.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {
    FileService fileService;
    private final RestClient.Builder builder;

    @PostMapping("/media/upload")
    ApiResponse<FileResponse> uploadMedia(@RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.<FileResponse>builder()
                .result(fileService.uploadFile(file))
                .build();
    }

    @GetMapping("/media/download/{fileName}")
    ResponseEntity<Resource> downloadMedia(@PathVariable String fileName) throws IOException {
        var fileData = fileService.downloadFile(fileName);


        return ResponseEntity.<Resource>ok()
                .header(HttpHeaders.CONTENT_TYPE, fileData.contentType())
                .body(fileData.resource());
    }
}