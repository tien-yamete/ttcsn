package com.tien.groupservice.dto.response;

import com.tien.groupservice.entity.GroupPrivacy;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
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
	String createdDate;
	String modifiedDate;
	boolean isMember;
	MemberRoleResponse memberRole;
}

