package com.tien.socialservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FollowResponse {
    String id;
    String followerId;
    String followeeId;
    LocalDateTime createdAt;
}
