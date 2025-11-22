package com.tien.chatservice.entity;

import com.tien.chatservice.constant.ParticipantRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    
    @Builder.Default
    ParticipantRole role = ParticipantRole.MEMBER;
}