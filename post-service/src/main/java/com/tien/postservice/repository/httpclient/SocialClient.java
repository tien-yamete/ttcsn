package com.tien.postservice.repository.httpclient;

import com.tien.postservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "social-service", url = "${app.services.social.url}")
public interface SocialClient {
    @GetMapping("/blocks/ids")
    ApiResponse<List<String>> getBlockedUserIds();
}

