package com.tien.identityservice.dto.request;

import com.tien.identityservice.validator.DobConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;
    String firstName;
    String lastName;

    String email;

    @DobConstraint(min = 13, message = "INVALID_DOB")
    LocalDate dob;

    List<String> roles;
}
