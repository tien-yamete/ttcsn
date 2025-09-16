package com.tien.userservice.dto.request;

import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {
    String firstName;
    String lastName;
    LocalDate dob;
    String city;
}
