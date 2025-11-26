package com.tien.postservice.entity;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@Document(value = "shared_posts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SharedPost {
    @MongoId
    String id;

    String userId; // Người share
    String postId; // ID của bài viết gốc
    String originalPostUserId; // ID của chủ bài viết gốc
    String content;
    Instant sharedDate;
}
