package com.tien.postservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

import com.tien.postservice.entity.PrivacyType;

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
    String created;
    List<String> imageUrls;
    PrivacyType privacy;
    Instant createdDate;
    Instant modifiedDate;
}
