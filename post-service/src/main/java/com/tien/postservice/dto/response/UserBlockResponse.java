package com.tien.postservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserBlockResponse {
    String id;
    String blockerId;
    String blockedId;
    LocalDateTime createdAt;
}

