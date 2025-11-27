package com.tien.groupservice.controller;

import com.tien.groupservice.dto.ApiResponse;
import com.tien.groupservice.service.GroupService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/internal")
public class InternalGroupController {
	GroupService groupService;

	@GetMapping("/groups/{groupId}/exists")
	ApiResponse<Boolean> checkGroupExists(@PathVariable String groupId) {
		boolean exists = groupService.checkGroupExists(groupId);
		return ApiResponse.<Boolean>builder()
				.code(200)
				.message("Check group exists")
				.result(exists)
				.build();
	}

	@GetMapping("/groups/{groupId}")
	ApiResponse<com.tien.groupservice.dto.response.GroupResponse> getGroup(@PathVariable String groupId) {
		return ApiResponse.<com.tien.groupservice.dto.response.GroupResponse>builder()
				.result(groupService.getGroupById(groupId))
				.build();
	}

	@GetMapping("/groups/{groupId}/can-post")
	ApiResponse<Boolean> canPost(@PathVariable String groupId) {
		return ApiResponse.<Boolean>builder()
				.result(groupService.canPost(groupId))
				.build();
	}

	@GetMapping("/groups/{groupId}/can-view")
	ApiResponse<Boolean> canViewPosts(@PathVariable String groupId) {
		return ApiResponse.<Boolean>builder()
				.result(groupService.canViewPosts(groupId))
				.build();
	}

	@GetMapping("/groups/{groupId}/can-view/{userId}")
	ApiResponse<Boolean> canViewPostsInternal(
			@PathVariable String groupId,
			@PathVariable String userId) {
		return ApiResponse.<Boolean>builder()
				.result(groupService.canViewPostsInternal(groupId, userId))
				.build();
	}
}

