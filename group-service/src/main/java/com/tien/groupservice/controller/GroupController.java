package com.tien.groupservice.controller;

import com.tien.groupservice.dto.ApiResponse;
import com.tien.groupservice.dto.PageResponse;
import com.tien.groupservice.dto.request.CreateGroupRequest;
import com.tien.groupservice.dto.request.JoinGroupRequest;
import com.tien.groupservice.dto.request.ProcessJoinRequest;
import com.tien.groupservice.dto.request.UpdateGroupRequest;
import com.tien.groupservice.dto.request.UpdateMemberRoleRequest;
import com.tien.groupservice.dto.response.GroupMemberResponse;
import com.tien.groupservice.dto.response.GroupResponse;
import com.tien.groupservice.dto.response.JoinRequestResponse;
import com.tien.groupservice.service.GroupService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupController {
	GroupService groupService;

	// Group CRUD
	@PostMapping
	ApiResponse<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request) {
		return ApiResponse.<GroupResponse>builder()
				.result(groupService.createGroup(request))
				.build();
	}

	@PutMapping("/{groupId}")
	ApiResponse<GroupResponse> updateGroup(
			@PathVariable String groupId,
			@Valid @RequestBody UpdateGroupRequest request) {
		return ApiResponse.<GroupResponse>builder()
				.result(groupService.updateGroup(groupId, request))
				.build();
	}

	@DeleteMapping("/{groupId}")
	ApiResponse<Void> deleteGroup(@PathVariable String groupId) {
		groupService.deleteGroup(groupId);
		return ApiResponse.<Void>builder().build();
	}

	@PutMapping(value = "/{groupId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ApiResponse<GroupResponse> uploadGroupAvatar(
			@PathVariable String groupId,
			@RequestParam("file") MultipartFile file) {
		return ApiResponse.<GroupResponse>builder()
				.result(groupService.uploadGroupAvatar(groupId, file))
				.build();
	}

	@PutMapping(value = "/{groupId}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ApiResponse<GroupResponse> uploadGroupCover(
			@PathVariable String groupId,
			@RequestParam("file") MultipartFile file) {
		return ApiResponse.<GroupResponse>builder()
				.result(groupService.uploadGroupCover(groupId, file))
				.build();
	}

	@GetMapping
	ApiResponse<PageResponse<GroupResponse>> getAllGroups(
			@RequestParam(value = "privacy", required = false) String privacy,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
		return ApiResponse.<PageResponse<GroupResponse>>builder()
				.result(groupService.getAllGroups(privacy, page, size))
				.build();
	}

	@GetMapping("/{groupId}")
	ApiResponse<GroupResponse> getGroupById(@PathVariable String groupId) {
		return ApiResponse.<GroupResponse>builder()
				.result(groupService.getGroupById(groupId))
				.build();
	}

	// Member management
	@PostMapping("/{groupId}/members/{userId}")
	ApiResponse<Void> addMember(
			@PathVariable String groupId,
			@PathVariable String userId) {
		groupService.addMember(groupId, userId);
		return ApiResponse.<Void>builder().build();
	}

	@DeleteMapping("/{groupId}/members/{userId}")
	ApiResponse<Void> removeMember(
			@PathVariable String groupId,
			@PathVariable String userId) {
		groupService.removeMember(groupId, userId);
		return ApiResponse.<Void>builder().build();
	}

	@PutMapping("/{groupId}/members/{userId}/role")
	ApiResponse<Void> updateMemberRole(
			@PathVariable String groupId,
			@PathVariable String userId,
			@Valid @RequestBody UpdateMemberRoleRequest request) {
		groupService.updateMemberRole(groupId, userId, request);
		return ApiResponse.<Void>builder().build();
	}

	@GetMapping("/{groupId}/members")
	ApiResponse<PageResponse<GroupMemberResponse>> getGroupMembers(
			@PathVariable String groupId,
			@RequestParam(value = "role", required = false) String role,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
		return ApiResponse.<PageResponse<GroupMemberResponse>>builder()
				.result(groupService.getGroupMembers(groupId, role, page, size))
				.build();
	}

	// Join group
	@PostMapping("/{groupId}/join")
	ApiResponse<Void> joinGroup(
			@PathVariable String groupId,
			@RequestBody(required = false) JoinGroupRequest request) {
		if (request == null) {
			request = JoinGroupRequest.builder().build();
		}
		groupService.joinGroup(groupId, request);
		return ApiResponse.<Void>builder().build();
	}

	@PostMapping("/{groupId}/leave")
	ApiResponse<Void> leaveGroup(@PathVariable String groupId) {
		groupService.leaveGroup(groupId);
		return ApiResponse.<Void>builder().build();
	}

	// Join request management
	@PostMapping("/{groupId}/join-requests/{requestId}/process")
	ApiResponse<Void> processJoinRequest(
			@PathVariable String groupId,
			@PathVariable String requestId,
			@Valid @RequestBody ProcessJoinRequest request) {
		groupService.processJoinRequest(groupId, requestId, request);
		return ApiResponse.<Void>builder().build();
	}

	@GetMapping("/{groupId}/join-requests")
	ApiResponse<PageResponse<JoinRequestResponse>> getJoinRequests(
			@PathVariable String groupId,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
		return ApiResponse.<PageResponse<JoinRequestResponse>>builder()
				.result(groupService.getJoinRequests(groupId, page, size))
				.build();
	}

	@DeleteMapping("/{groupId}/join-requests/{requestId}")
	ApiResponse<Void> cancelJoinRequest(
			@PathVariable String groupId,
			@PathVariable String requestId) {
		groupService.cancelJoinRequest(groupId, requestId);
		return ApiResponse.<Void>builder().build();
	}

	@GetMapping("/my-join-requests")
	ApiResponse<PageResponse<JoinRequestResponse>> getMyJoinRequests(
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
		return ApiResponse.<PageResponse<JoinRequestResponse>>builder()
				.result(groupService.getMyJoinRequests(page, size))
				.build();
	}

	// Query operations
	@GetMapping("/my-groups")
	ApiResponse<PageResponse<GroupResponse>> getMyGroups(
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
		return ApiResponse.<PageResponse<GroupResponse>>builder()
				.result(groupService.getMyGroups(page, size))
				.build();
	}

	@GetMapping("/joined-groups")
	ApiResponse<PageResponse<GroupResponse>> getJoinedGroups(
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
		return ApiResponse.<PageResponse<GroupResponse>>builder()
				.result(groupService.getJoinedGroups(page, size))
				.build();
	}

	@GetMapping("/search")
	ApiResponse<PageResponse<GroupResponse>> searchGroups(
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
		return ApiResponse.<PageResponse<GroupResponse>>builder()
				.result(groupService.searchGroups(keyword, page, size))
				.build();
	}

}

