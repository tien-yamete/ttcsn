package com.tien.socialservice.dto.response;

import java.time.LocalDateTime;

import com.tien.socialservice.entity.FriendshipStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendshipResponse {
    String id;
    String userId;
    String friendId;
    FriendshipStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
