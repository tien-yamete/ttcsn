package com.tien.postservice.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    String id;
    String userId;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
    String city;
    String country;
    String bio;
    String phoneNumber;
    String gender;
    String website;
    String avatar;
    String backgroundImage;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
