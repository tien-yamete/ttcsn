package com.tien.groupservice.dto.response;

import com.tien.groupservice.entity.GroupPrivacy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupResponse {
	String id;
	String name;
	String description;
	String coverImageUrl;
	String avatarUrl;
	String ownerId;
	String ownerName;
	String ownerAvatar;
	GroupPrivacy privacy;
	boolean requiresApproval;
	boolean allowPosting;
	boolean moderationRequired;
	boolean onlyAdminCanPost;
	long memberCount;
	long pendingRequestCount; // Số lượng join requests đang chờ (chỉ admin/moderator thấy)
	String createdDate;
	String modifiedDate;
	boolean isMember;
	MemberRoleResponse memberRole;
}

