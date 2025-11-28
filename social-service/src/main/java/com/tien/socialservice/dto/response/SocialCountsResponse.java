package com.tien.socialservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocialCountsResponse {
    long friendsCount;
    long followersCount;
    long followingCount;
    long pendingFriendRequestsCount;
    long sentFriendRequestsCount;
    long blockedUsersCount;
}

