package com.tien.groupservice.dto.response;

import com.tien.groupservice.entity.MemberRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupMemberResponse {
	String id;
	String userId;
	String username;
	String avatar;
	MemberRole role;
	String joinedDate;
}

