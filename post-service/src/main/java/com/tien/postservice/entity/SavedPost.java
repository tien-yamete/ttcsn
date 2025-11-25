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
@Document(value = "saved_posts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SavedPost {
    @MongoId
    String id;

    String userId;
    String postId;
    Instant savedDate;
}
