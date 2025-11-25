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
@Document(value = "group_member")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupMember {
	@MongoId
	String id;
	String groupId;
	String userId;
	MemberRole role; // ADMIN, MODERATOR, MEMBER
	Instant joinedDate;
}

