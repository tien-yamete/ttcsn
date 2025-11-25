package com.tien.postservice.dto.response;

import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
