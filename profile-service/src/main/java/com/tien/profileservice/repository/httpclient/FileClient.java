package com.tien.profileservice.repository.httpclient;

import com.tien.profileservice.configuration.AuthenticationRequestInterceptor;
import com.tien.profileservice.dto.ApiResponse;
import com.tien.profileservice.dto.response.FileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", url = "${app.services.file}",
        configuration = { AuthenticationRequestInterceptor.class })
public interface FileClient {
    @PostMapping(value = "/file/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<FileResponse> uploadFile(@RequestPart("file") MultipartFile file);
}

