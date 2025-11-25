package com.tien.notificationservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {
	@Id
	String id;

	String userId; // User nhận notification
	String type; // FRIEND_REQUEST, POST_LIKE, POST_COMMENT, MESSAGE, etc.
	String title; // Tiêu đề notification
	String content; // Nội dung notification
	String relatedUserId; // User liên quan (người like, comment, etc.)
	String relatedEntityId; // ID của entity liên quan (postId, commentId, etc.)
	String relatedEntityType; // POST, COMMENT, MESSAGE, etc.

	Boolean isRead; // Đã đọc chưa
	Instant createdAt; // Thời gian tạo
	Instant readAt; // Thời gian đọc
}

