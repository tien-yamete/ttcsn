package com.tien.socialservice.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
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
