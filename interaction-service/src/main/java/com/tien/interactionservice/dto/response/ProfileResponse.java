package com.tien.interactionservice.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    String userId;
    String username;
    String avatar;
    String firstName;
    String lastName;
    String email;
    String bio;
    String city;
    String country;
    String phoneNumber;
    String gender;
    String website;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

