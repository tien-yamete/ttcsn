package com.tien.groupservice.dto.response;

import com.tien.groupservice.entity.RequestStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JoinRequestResponse {
	String id;
	String groupId;
	String groupName;
	String userId;
	String username;
	String avatar;
	RequestStatus status;
	String message;
	String requestedDate;
	String reviewedDate;
	String reviewedBy;
}

