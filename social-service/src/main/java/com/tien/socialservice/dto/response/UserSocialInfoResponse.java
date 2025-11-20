package com.tien.socialservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSocialInfoResponse {
    String userId;
    long followersCount;
    long followingCount;
    long friendsCount;
    boolean isFollowing;
    boolean isFriend;
    boolean isBlocked;
}
