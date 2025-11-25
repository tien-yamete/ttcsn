package com.tien.groupservice.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Getter
@Setter
@Builder
@Document(value = "group")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Group {
	@MongoId
	String id;
	String name;
	String description;
	String coverImageUrl;
	String avatarUrl;
	String ownerId; // Người tạo group
	GroupPrivacy privacy; // PUBLIC, PRIVATE, CLOSED
	boolean requiresApproval; // Cần phê duyệt khi tham gia
	boolean allowPosting; // Cho phép đăng bài
	boolean moderationRequired; // Cần kiểm duyệt bài đăng
	boolean onlyAdminCanPost; // Chỉ admin/moderator mới được đăng
	Instant createdDate;
	Instant modifiedDate;
}

