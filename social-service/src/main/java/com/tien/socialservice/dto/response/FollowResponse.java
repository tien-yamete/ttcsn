package com.tien.socialservice.dto.response;

import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FollowResponse {
    String id;
    String followerId;
    String followingId;
    LocalDateTime createdAt;
}
