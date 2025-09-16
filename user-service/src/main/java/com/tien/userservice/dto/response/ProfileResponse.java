package com.tien.userservice.dto.response;

import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
}
