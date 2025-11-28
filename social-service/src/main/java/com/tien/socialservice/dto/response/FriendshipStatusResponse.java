package com.tien.socialservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendshipStatusResponse {
    Map<String, String> statuses; // userId -> status (ACCEPTED, PENDING, SENT, RECEIVED, NONE, SELF)
}

