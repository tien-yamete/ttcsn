package com.tien.postservice.dto.response;

import java.time.Instant;
import java.util.List;

import com.tien.postservice.entity.PrivacyType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    String id;
    String content;
    String userId;
    String username;
    String userAvatar;
    String created;
    List<String> imageUrls;
    PrivacyType privacy;
    Instant createdDate;
    Instant modifiedDate;

    // Group info
    String groupId;
    String groupName;

    // Interaction stats
    Integer likeCount;
    Integer commentCount;
    Boolean isLiked;
    Boolean isSaved;
    Boolean isOwnerPost;
    Long shareCount;

    // Original post info (khi là shared post)
    String originalPostUserId;
    String originalPostUsername;
    String originalPostUserAvatar;
}
