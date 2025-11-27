package com.tien.groupservice.service;

import com.tien.groupservice.dto.PageResponse;
import com.tien.groupservice.dto.request.CreateGroupRequest;
import com.tien.groupservice.dto.request.JoinGroupRequest;
import com.tien.groupservice.dto.request.ProcessJoinRequest;
import com.tien.groupservice.dto.request.UpdateGroupRequest;
import com.tien.groupservice.dto.request.UpdateMemberRoleRequest;
import com.tien.groupservice.dto.response.GroupMemberResponse;
import com.tien.groupservice.dto.response.GroupResponse;
import com.tien.groupservice.dto.response.JoinRequestResponse;
import com.tien.groupservice.dto.response.MemberRoleResponse;
import com.tien.groupservice.dto.response.UserProfileResponse;
import com.tien.groupservice.entity.Group;
import com.tien.groupservice.entity.GroupMember;
import com.tien.groupservice.entity.GroupPrivacy;
import com.tien.groupservice.entity.JoinRequest;
import com.tien.groupservice.entity.MemberRole;
import com.tien.groupservice.entity.RequestStatus;
import com.tien.groupservice.exception.AppException;
import com.tien.groupservice.exception.ErrorCode;
import com.tien.groupservice.mapper.GroupMapper;
import com.tien.groupservice.mapper.GroupMemberMapper;
import com.tien.groupservice.mapper.JoinRequestMapper;
import com.tien.groupservice.repository.GroupMemberRepository;
import com.tien.groupservice.repository.GroupRepository;
import com.tien.groupservice.repository.JoinRequestRepository;
import com.tien.groupservice.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupService {
	GroupRepository groupRepository;
	GroupMemberRepository groupMemberRepository;
	JoinRequestRepository joinRequestRepository;
	GroupMapper groupMapper;
	GroupMemberMapper groupMemberMapper;
	JoinRequestMapper joinRequestMapper;
	ProfileClient profileClient;
	DateTimeFormatter dateTimeFormatter;
	ImageUploadKafkaService imageUploadKafkaService;

	// Group CRUD operations
	@Transactional
	public GroupResponse createGroup(CreateGroupRequest request) {
		String userId = getCurrentUserId();

		if (request.getName() == null || request.getName().trim().isEmpty()) {
			throw new AppException(ErrorCode.GROUP_NAME_REQUIRED);
		}

		Group group = Group.builder()
				.name(request.getName())
				.description(request.getDescription())
				.coverImageUrl(request.getCoverImageUrl())
				.avatarUrl(request.getAvatarUrl())
				.ownerId(userId)
				.privacy(request.getPrivacy() != null ? request.getPrivacy() : GroupPrivacy.PUBLIC)
				.requiresApproval(request.getRequiresApproval() != null ? request.getRequiresApproval() : false)
				.allowPosting(request.getAllowPosting() != null ? request.getAllowPosting() : true)
				.moderationRequired(request.getModerationRequired() != null ? request.getModerationRequired() : false)
				.onlyAdminCanPost(request.getOnlyAdminCanPost() != null ? request.getOnlyAdminCanPost() : false)
				.createdDate(Instant.now())
				.modifiedDate(Instant.now())
				.build();

		group = groupRepository.save(group);

		// Owner automatically becomes ADMIN member
		GroupMember ownerMember = GroupMember.builder()
				.groupId(group.getId())
				.userId(userId)
				.role(MemberRole.ADMIN)
				.joinedDate(Instant.now())
				.build();
		groupMemberRepository.save(ownerMember);

		return buildGroupResponse(group, userId);
	}

	@Transactional
	public GroupResponse updateGroup(String groupId, UpdateGroupRequest request) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Chỉ owner mới được update
		if (!group.getOwnerId().equals(userId)) {
			throw new AppException(ErrorCode.GROUP_NOT_OWNER);
		}

		if (request.getName() != null && !request.getName().trim().isEmpty()) {
			group.setName(request.getName());
		}
		if (request.getDescription() != null) {
			group.setDescription(request.getDescription());
		}
		if (request.getPrivacy() != null) {
			group.setPrivacy(request.getPrivacy());
		}
		if (request.getRequiresApproval() != null) {
			group.setRequiresApproval(request.getRequiresApproval());
		}
		if (request.getAllowPosting() != null) {
			group.setAllowPosting(request.getAllowPosting());
		}
		if (request.getModerationRequired() != null) {
			group.setModerationRequired(request.getModerationRequired());
		}
		if (request.getOnlyAdminCanPost() != null) {
			group.setOnlyAdminCanPost(request.getOnlyAdminCanPost());
		}

		group.setModifiedDate(Instant.now());
		group = groupRepository.save(group);

		return buildGroupResponse(group, userId);
	}

	@Transactional
	public GroupResponse uploadGroupAvatar(String groupId, MultipartFile file) {
		// Validate auth
		String userId = getCurrentUserId();
		if (userId == null || userId.trim().isEmpty()) {
			throw new AppException(ErrorCode.UNAUTHORIZED);
		}

		// Validate file
		if (file == null || file.isEmpty()) {
			throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
		}

		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Chỉ owner hoặc admin mới được upload avatar
		if (!group.getOwnerId().equals(userId)) {
			// Kiểm tra xem user có phải admin không
			GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
					.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_OWNER));
			if (member.getRole() != MemberRole.ADMIN) {
				throw new AppException(ErrorCode.GROUP_NOT_OWNER);
			}
		}

		// Upload file using Kafka
		try {
			var uploadedEvent = imageUploadKafkaService.uploadGroupAvatar(file, groupId);
			group.setAvatarUrl(uploadedEvent.imageUrl());
		} catch (Exception e) {
			log.error("Failed to upload group avatar via Kafka: {}", e.getMessage(), e);
			throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
		}

		group.setModifiedDate(Instant.now());
		return buildGroupResponse(groupRepository.save(group), userId);
	}

	@Transactional
	public GroupResponse uploadGroupCover(String groupId, MultipartFile file) {
		// Validate auth
		String userId = getCurrentUserId();
		if (userId == null || userId.trim().isEmpty()) {
			throw new AppException(ErrorCode.UNAUTHORIZED);
		}

		// Validate file
		if (file == null || file.isEmpty()) {
			throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
		}

		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Chỉ owner hoặc admin mới được upload cover
		if (!group.getOwnerId().equals(userId)) {
			// Kiểm tra xem user có phải admin không
			GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
					.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_OWNER));
			if (member.getRole() != MemberRole.ADMIN) {
				throw new AppException(ErrorCode.GROUP_NOT_OWNER);
			}
		}

		// Upload file using Kafka
		try {
			var uploadedEvent = imageUploadKafkaService.uploadGroupCover(file, groupId);
			group.setCoverImageUrl(uploadedEvent.imageUrl());
		} catch (Exception e) {
			log.error("Failed to upload group cover via Kafka: {}", e.getMessage(), e);
			throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
		}

		group.setModifiedDate(Instant.now());
		return buildGroupResponse(groupRepository.save(group), userId);
	}

	public void deleteGroup(String groupId) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Chỉ owner mới được xóa
		if (!group.getOwnerId().equals(userId)) {
			throw new AppException(ErrorCode.GROUP_NOT_OWNER);
		}

		// Xóa tất cả members
		groupMemberRepository.deleteAllByGroupId(groupId);
		// Xóa tất cả join requests
		joinRequestRepository.deleteAllByGroupId(groupId);
		// Xóa group
		groupRepository.delete(group);
	}

	public PageResponse<GroupResponse> getAllGroups(String privacy, int page, int size) {
		String userId = getCurrentUserId();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());

		Page<Group> pageData;
		
		// Nếu có filter theo privacy
		if (privacy != null && !privacy.trim().isEmpty()) {
			try {
				GroupPrivacy privacyFilter = GroupPrivacy.valueOf(privacy.toUpperCase());
				// Chỉ cho phép lấy PUBLIC và CLOSED, không cho lấy PRIVATE
				if (privacyFilter == GroupPrivacy.PRIVATE) {
					throw new AppException(ErrorCode.UNAUTHORIZED);
				}
				pageData = groupRepository.findByPrivacy(privacyFilter, pageable);
			} catch (IllegalArgumentException e) {
				throw new AppException(ErrorCode.INVALID_KEY);
			}
		} else {
			// Mặc định lấy PUBLIC và CLOSED groups
			List<GroupPrivacy> searchablePrivacies = List.of(GroupPrivacy.PUBLIC, GroupPrivacy.CLOSED);
			pageData = groupRepository.findByPrivacyIn(searchablePrivacies, pageable);
		}

		var groupList = pageData.getContent().stream()
				.map(group -> buildGroupResponse(group, userId))
				.toList();

		return PageResponse.<GroupResponse>builder()
				.currentPage(page)
				.pageSize(pageData.getSize())
				.totalPages(pageData.getTotalPages())
				.totalElements(pageData.getTotalElements())
				.data(groupList)
				.build();
	}

	public GroupResponse getGroupById(String groupId) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Kiểm tra quyền xem group
		if (group.getPrivacy() == GroupPrivacy.PRIVATE) {
			if (!group.getOwnerId().equals(userId) && !isMember(groupId, userId)) {
				throw new AppException(ErrorCode.UNAUTHORIZED);
			}
		}

		return buildGroupResponse(group, userId);
	}

	// Member management
	@Transactional
	public void addMember(String groupId, String memberUserId) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Kiểm tra quyền: chỉ admin/moderator mới được thêm thành viên
		checkAdminOrModeratorPermission(groupId, userId);

		// Kiểm tra đã là member chưa
		if (groupMemberRepository.existsByGroupIdAndUserId(groupId, memberUserId)) {
			throw new AppException(ErrorCode.MEMBER_ALREADY_EXISTS);
		}

		GroupMember member = GroupMember.builder()
				.groupId(groupId)
				.userId(memberUserId)
				.role(MemberRole.MEMBER)
				.joinedDate(Instant.now())
				.build();
		groupMemberRepository.save(member);

		// Xóa join request nếu có
		joinRequestRepository.findByGroupIdAndUserId(groupId, memberUserId).ifPresent(joinRequestRepository::delete);
	}

	@Transactional
	public void removeMember(String groupId, String memberUserId) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Không thể xóa owner
		if (group.getOwnerId().equals(memberUserId)) {
			throw new AppException(ErrorCode.MEMBER_CANNOT_REMOVE_OWNER);
		}

		// Kiểm tra quyền: chỉ admin/moderator hoặc chính thành viên đó mới được xóa
		GroupMember currentMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
				.orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

		GroupMember memberToRemove = groupMemberRepository.findByGroupIdAndUserId(groupId, memberUserId)
				.orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

		// Chỉ có thể xóa nếu là admin/moderator hoặc chính thành viên đó
		if (!currentMember.getRole().equals(MemberRole.ADMIN)
				&& !currentMember.getRole().equals(MemberRole.MODERATOR)
				&& !userId.equals(memberUserId)) {
			throw new AppException(ErrorCode.INSUFFICIENT_PERMISSION);
		}

		groupMemberRepository.deleteByGroupIdAndUserId(groupId, memberUserId);
	}

	public void updateMemberRole(String groupId, String memberUserId, UpdateMemberRoleRequest request) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Chỉ admin mới được thay đổi role
		checkAdminPermission(groupId, userId);

		// Không thể thay đổi role của owner
		if (group.getOwnerId().equals(memberUserId)) {
			throw new AppException(ErrorCode.CANNOT_CHANGE_OWNER_ROLE);
		}

		GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, memberUserId)
				.orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

		if (request.getRole() == null) {
			throw new AppException(ErrorCode.INVALID_ROLE);
		}

		member.setRole(request.getRole());
		groupMemberRepository.save(member);
	}

	// Join group
	@Transactional
	public void joinGroup(String groupId, JoinGroupRequest request) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Kiểm tra đã là member chưa
		if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
			throw new AppException(ErrorCode.ALREADY_MEMBER);
		}

		// Nếu group không cần phê duyệt, tự động thêm member
		if (!group.isRequiresApproval()) {
			GroupMember member = GroupMember.builder()
					.groupId(groupId)
					.userId(userId)
					.role(MemberRole.MEMBER)
					.joinedDate(Instant.now())
					.build();
			groupMemberRepository.save(member);
			return;
		}

		// Nếu cần phê duyệt, tạo join request
		if (joinRequestRepository.existsByGroupIdAndUserId(groupId, userId)) {
			throw new AppException(ErrorCode.JOIN_REQUEST_ALREADY_EXISTS);
		}

		JoinRequest joinRequest = JoinRequest.builder()
				.groupId(groupId)
				.userId(userId)
				.status(RequestStatus.PENDING)
				.message(request.getMessage())
				.requestedDate(Instant.now())
				.build();
		joinRequestRepository.save(joinRequest);
	}

	public void leaveGroup(String groupId) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Owner không thể rời group
		if (group.getOwnerId().equals(userId)) {
			throw new AppException(ErrorCode.MEMBER_CANNOT_REMOVE_OWNER);
		}

		if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
			throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
		}

		groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
	}

	// Join request management
	@Transactional
	public void processJoinRequest(String groupId, String requestId, ProcessJoinRequest request) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Kiểm tra quyền: chỉ admin/moderator mới được xử lý
		checkAdminOrModeratorPermission(groupId, userId);

		JoinRequest joinRequest = joinRequestRepository.findById(requestId)
				.filter(jr -> jr.getGroupId().equals(groupId))
				.orElseThrow(() -> new AppException(ErrorCode.JOIN_REQUEST_NOT_FOUND));

		if (joinRequest.getStatus() != RequestStatus.PENDING) {
			throw new AppException(ErrorCode.JOIN_REQUEST_NOT_FOUND);
		}

		if (Boolean.TRUE.equals(request.getApprove())) {
			// Approve: thêm member và cập nhật request
			if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, joinRequest.getUserId())) {
				GroupMember member = GroupMember.builder()
						.groupId(groupId)
						.userId(joinRequest.getUserId())
						.role(MemberRole.MEMBER)
						.joinedDate(Instant.now())
						.build();
				groupMemberRepository.save(member);
			}
			joinRequest.setStatus(RequestStatus.APPROVED);
		} else {
			// Reject
			joinRequest.setStatus(RequestStatus.REJECTED);
		}

		joinRequest.setReviewedDate(Instant.now());
		joinRequest.setReviewedBy(userId);
		joinRequestRepository.save(joinRequest);
	}

	// Query operations
	public PageResponse<GroupResponse> getMyGroups(int page, int size) {
		String userId = getCurrentUserId();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
		var pageData = groupRepository.findByOwnerId(userId, pageable);

		var groupList = pageData.getContent().stream()
				.map(group -> buildGroupResponse(group, userId))
				.toList();

		return PageResponse.<GroupResponse>builder()
				.currentPage(page)
				.pageSize(pageData.getSize())
				.totalPages(pageData.getTotalPages())
				.totalElements(pageData.getTotalElements())
				.data(groupList)
				.build();
	}

	public PageResponse<GroupResponse> getJoinedGroups(int page, int size) {
		String userId = getCurrentUserId();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("joinedDate").descending());
		var pageData = groupMemberRepository.findByUserId(userId, pageable);

		List<String> groupIds = pageData.getContent().stream()
				.map(GroupMember::getGroupId)
				.toList();

		if (groupIds.isEmpty()) {
			return PageResponse.<GroupResponse>builder()
					.currentPage(page)
					.pageSize(pageData.getSize())
					.totalPages(pageData.getTotalPages())
					.totalElements(pageData.getTotalElements())
					.data(List.of())
					.build();
		}

		List<Group> groups = groupRepository.findAllById(groupIds);
		var groupMap = groups.stream().collect(Collectors.toMap(Group::getId, group -> group));

		var groupList = pageData.getContent().stream()
				.map(member -> {
					Group group = groupMap.get(member.getGroupId());
					return group != null ? buildGroupResponse(group, userId) : null;
				})
				.filter(group -> group != null)
				.toList();

		return PageResponse.<GroupResponse>builder()
				.currentPage(page)
				.pageSize(pageData.getSize())
				.totalPages(pageData.getTotalPages())
				.totalElements(pageData.getTotalElements())
				.data(groupList)
				.build();
	}

	public PageResponse<GroupResponse> searchGroups(String keyword, int page, int size) {
		String userId = getCurrentUserId();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());

		// Chỉ tìm PUBLIC và CLOSED groups (không tìm PRIVATE)
		List<GroupPrivacy> searchablePrivacies = List.of(GroupPrivacy.PUBLIC, GroupPrivacy.CLOSED);
		var pageData = keyword != null && !keyword.trim().isEmpty()
				? groupRepository.searchGroupsWithPrivacy(searchablePrivacies, keyword.trim(), pageable)
				: groupRepository.findByPrivacyIn(searchablePrivacies, pageable);

		var groupList = pageData.getContent().stream()
				.map(group -> buildGroupResponse(group, userId))
				.toList();

		return PageResponse.<GroupResponse>builder()
				.currentPage(page)
				.pageSize(pageData.getSize())
				.totalPages(pageData.getTotalPages())
				.totalElements(pageData.getTotalElements())
				.data(groupList)
				.build();
	}

	public PageResponse<GroupMemberResponse> getGroupMembers(String groupId, String role, int page, int size) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Kiểm tra quyền xem members
		if (group.getPrivacy() == GroupPrivacy.PRIVATE && !isMember(groupId, userId) && !group.getOwnerId().equals(userId)) {
			throw new AppException(ErrorCode.UNAUTHORIZED);
		}

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("joinedDate").ascending());
		Page<GroupMember> pageData;

		// Filter theo role nếu có
		if (role != null && !role.trim().isEmpty()) {
			try {
				MemberRole memberRole = MemberRole.valueOf(role.toUpperCase());
				pageData = groupMemberRepository.findByGroupIdAndRole(groupId, memberRole, pageable);
			} catch (IllegalArgumentException e) {
				throw new AppException(ErrorCode.INVALID_ROLE);
			}
		} else {
			pageData = groupMemberRepository.findByGroupId(groupId, pageable);
		}

		var memberList = pageData.getContent().stream()
				.map(member -> buildGroupMemberResponse(member))
				.toList();

		return PageResponse.<GroupMemberResponse>builder()
				.currentPage(page)
				.pageSize(pageData.getSize())
				.totalPages(pageData.getTotalPages())
				.totalElements(pageData.getTotalElements())
				.data(memberList)
				.build();
	}

	public PageResponse<JoinRequestResponse> getJoinRequests(String groupId, int page, int size) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Chỉ admin/moderator mới xem được join requests
		checkAdminOrModeratorPermission(groupId, userId);

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("requestedDate").descending());
		var pageData = joinRequestRepository.findByGroupIdAndStatus(groupId, RequestStatus.PENDING, pageable);

		var requestList = pageData.getContent().stream()
				.map(this::buildJoinRequestResponse)
				.toList();

		return PageResponse.<JoinRequestResponse>builder()
				.currentPage(page)
				.pageSize(pageData.getSize())
				.totalPages(pageData.getTotalPages())
				.totalElements(pageData.getTotalElements())
				.data(requestList)
				.build();
	}

	@Transactional
	public void cancelJoinRequest(String groupId, String requestId) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		JoinRequest joinRequest = joinRequestRepository.findById(requestId)
				.filter(jr -> jr.getGroupId().equals(groupId))
				.orElseThrow(() -> new AppException(ErrorCode.JOIN_REQUEST_NOT_FOUND));

		// Chỉ người gửi request mới được hủy
		if (!joinRequest.getUserId().equals(userId)) {
			throw new AppException(ErrorCode.UNAUTHORIZED);
		}

		// Chỉ hủy được request đang PENDING
		if (joinRequest.getStatus() != RequestStatus.PENDING) {
			throw new AppException(ErrorCode.JOIN_REQUEST_NOT_FOUND);
		}

		joinRequestRepository.delete(joinRequest);
	}

	public PageResponse<JoinRequestResponse> getMyJoinRequests(int page, int size) {
		String userId = getCurrentUserId();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("requestedDate").descending());
		var pageData = joinRequestRepository.findByUserId(userId, pageable);

		var requestList = pageData.getContent().stream()
				.map(this::buildJoinRequestResponse)
				.toList();

		return PageResponse.<JoinRequestResponse>builder()
				.currentPage(page)
				.pageSize(pageData.getSize())
				.totalPages(pageData.getTotalPages())
				.totalElements(pageData.getTotalElements())
				.data(requestList)
				.build();
	}

	// Permission checking
	public boolean canPost(String groupId) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// Group không cho phép đăng bài
		if (!group.isAllowPosting()) {
			return false;
		}

		// Nếu chỉ admin/moderator được đăng
		if (group.isOnlyAdminCanPost()) {
			return isAdminOrModerator(groupId, userId);
		}

		// Kiểm tra phải là member
		return isMember(groupId, userId);
	}

	/**
	 * Kiểm tra quyền xem post trong group dựa trên privacy của group
	 * - PUBLIC: Ai cũng xem được
	 * - PRIVATE: Chỉ members mới xem được
	 * - CLOSED: Ai cũng xem được (nhưng cần join mới tham gia)
	 */
	public boolean canViewPosts(String groupId) {
		String userId = getCurrentUserId();
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new AppException(ErrorCode.GROUP_NOT_FOUND));

		// PUBLIC và CLOSED: ai cũng xem được
		if (group.getPrivacy() == GroupPrivacy.PUBLIC || group.getPrivacy() == GroupPrivacy.CLOSED) {
			return true;
		}

		// PRIVATE: chỉ members mới xem được
		if (group.getPrivacy() == GroupPrivacy.PRIVATE) {
			return isMember(groupId, userId) || group.getOwnerId().equals(userId);
		}

		return false;
	}

	/**
	 * Kiểm tra quyền xem post trong group (internal API, không cần authentication context)
	 */
	public boolean canViewPostsInternal(String groupId, String userId) {
		if (groupId == null || userId == null) {
			return false;
		}

		Group group = groupRepository.findById(groupId)
				.orElse(null);

		if (group == null) {
			return false;
		}

		// PUBLIC và CLOSED: ai cũng xem được
		if (group.getPrivacy() == GroupPrivacy.PUBLIC || group.getPrivacy() == GroupPrivacy.CLOSED) {
			return true;
		}

		// PRIVATE: chỉ members mới xem được
		if (group.getPrivacy() == GroupPrivacy.PRIVATE) {
			return isMember(groupId, userId) || group.getOwnerId().equals(userId);
		}

		return false;
	}

	public boolean checkGroupExists(String groupId) {
		return groupRepository.existsById(groupId);
	}

	// Helper methods
	private String getCurrentUserId() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private boolean isMember(String groupId, String userId) {
		return groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
	}

	private boolean isAdminOrModerator(String groupId, String userId) {
		return groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
				.map(member -> member.getRole() == MemberRole.ADMIN || member.getRole() == MemberRole.MODERATOR)
				.orElse(false);
	}

	private boolean isAdmin(String groupId, String userId) {
		return groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
				.map(member -> member.getRole() == MemberRole.ADMIN)
				.orElse(false);
	}

	private void checkAdminPermission(String groupId, String userId) {
		if (!isAdmin(groupId, userId)) {
			throw new AppException(ErrorCode.INSUFFICIENT_PERMISSION);
		}
	}

	private void checkAdminOrModeratorPermission(String groupId, String userId) {
		if (!isAdminOrModerator(groupId, userId)) {
			throw new AppException(ErrorCode.INSUFFICIENT_PERMISSION);
		}
	}

	private UserProfileResponse getUserProfile(String userId) {
		try {
			var response = profileClient.getProfile(userId);
			return response != null && response.getResult() != null ? response.getResult() : null;
		} catch (Exception e) {
			log.error("Error while getting user profile for userId: {}", userId, e);
			return null;
		}
	}

	private GroupResponse buildGroupResponse(Group group, String currentUserId) {
		GroupResponse response = groupMapper.toGroupResponse(group);

		// Set owner info
		UserProfileResponse ownerProfile = getUserProfile(group.getOwnerId());
		if (ownerProfile != null) {
			response.setOwnerName(getDisplayName(ownerProfile.getFirstName(), ownerProfile.getLastName(), ownerProfile.getUsername()));
			response.setOwnerAvatar(ownerProfile.getAvatar());
		}

		// Set member count
		long memberCount = groupMemberRepository.countByGroupId(group.getId());
		response.setMemberCount(memberCount);

		// Set dates
		response.setCreatedDate(dateTimeFormatter.format(group.getCreatedDate()));
		response.setModifiedDate(dateTimeFormatter.format(group.getModifiedDate()));

		// Check if current user is member
		response.setMember(isMember(group.getId(), currentUserId));

		// Set member role if is member
		if (response.isMember()) {
			groupMemberRepository.findByGroupIdAndUserId(group.getId(), currentUserId)
					.ifPresent(member -> {
						MemberRoleResponse roleResponse = MemberRoleResponse.builder()
								.role(member.getRole().name())
								.joinedDate(dateTimeFormatter.format(member.getJoinedDate()))
								.build();
						response.setMemberRole(roleResponse);
					});
		}

		return response;
	}

	private GroupMemberResponse buildGroupMemberResponse(GroupMember member) {
		GroupMemberResponse response = groupMemberMapper.toGroupMemberResponse(member);

		UserProfileResponse profile = getUserProfile(member.getUserId());
		if (profile != null) {
			response.setUsername(getDisplayName(profile.getFirstName(), profile.getLastName(), profile.getUsername()));
			response.setAvatar(profile.getAvatar());
		}

		response.setJoinedDate(dateTimeFormatter.format(member.getJoinedDate()));

		return response;
	}

	private JoinRequestResponse buildJoinRequestResponse(JoinRequest joinRequest) {
		JoinRequestResponse response = joinRequestMapper.toJoinRequestResponse(joinRequest);

		Group group = groupRepository.findById(joinRequest.getGroupId()).orElse(null);
		if (group != null) {
			response.setGroupName(group.getName());
		}

		UserProfileResponse profile = getUserProfile(joinRequest.getUserId());
		if (profile != null) {
			response.setUsername(getDisplayName(profile.getFirstName(), profile.getLastName(), profile.getUsername()));
			response.setAvatar(profile.getAvatar());
		}

		response.setRequestedDate(dateTimeFormatter.format(joinRequest.getRequestedDate()));
		if (joinRequest.getReviewedDate() != null) {
			response.setReviewedDate(dateTimeFormatter.format(joinRequest.getReviewedDate()));
		}

		return response;
	}

	private String getDisplayName(String firstName, String lastName, String username) {
		// Nếu có cả firstName và lastName, hiển thị "firstName lastName"
		if (firstName != null && !firstName.trim().isEmpty() && lastName != null && !lastName.trim().isEmpty()) {
			return (firstName.trim() + " " + lastName.trim()).trim();
		}
		// Nếu chỉ có lastName, hiển thị lastName (thường là username)
		else if (lastName != null && !lastName.trim().isEmpty()) {
			return lastName.trim();
		}
		// Nếu chỉ có firstName, hiển thị firstName
		else if (firstName != null && !firstName.trim().isEmpty()) {
			return firstName.trim();
		}
		// Fallback to username
		else {
			return username != null ? username : "";
		}
	}
}

