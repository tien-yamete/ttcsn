package com.tien.chatservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipantInfo {
    String userId;
    String username;
    String firstName;
    String lastName;
    String avatar;
}

