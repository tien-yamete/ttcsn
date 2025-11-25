package com.tien.postservice.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tien.postservice.configuration.FeignConfig;
import com.tien.postservice.dto.ApiResponse;
import com.tien.postservice.dto.response.GroupResponse;

@FeignClient(name = "group-service", url = "${app.services.group.url}", configuration = FeignConfig.class)
public interface GroupClient {
    @GetMapping("/internal/groups/{groupId}/exists")
    ApiResponse<Boolean> checkGroupExists(@PathVariable String groupId);

    @GetMapping("/internal/groups/{groupId}/can-post")
    ApiResponse<Boolean> canPost(@PathVariable String groupId);

    @GetMapping("/internal/groups/{groupId}")
    ApiResponse<GroupResponse> getGroup(@PathVariable String groupId);
}
