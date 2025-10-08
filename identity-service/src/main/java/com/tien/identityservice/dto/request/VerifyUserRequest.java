package com.tien.identityservice.dto.request;

import com.tien.identityservice.constant.OtpType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class VerifyUserRequest {
    @NotBlank
    String email;

    @NotBlank
    String otpCode;
}

