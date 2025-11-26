package com.tien.postservice.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@Document(value = "post")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post {
    @MongoId
    String id;

    String userId;
    String content;
    List<String> imageUrls;
    PrivacyType privacy;
    String groupId; // null nếu là post thông thường, có giá trị nếu là post trong group
    String originalPostId; // null nếu là post thông thường, có giá trị nếu là shared post (ID của bài viết gốc)
    Instant createdDate;
    Instant modifiedDate;
}
