package com.tien.identityservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // ==========================
    // 🔹 General
    // ==========================
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),

    // ==========================
    // 🔹 User
    // ==========================
    USER_EXISTED(1101, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1102, "User not found", HttpStatus.NOT_FOUND),
    USER_DISABLED(1103, "User disabled", HttpStatus.FORBIDDEN),
    USER_ALREADY_VERIFIED(1104, "User already verified", HttpStatus.BAD_REQUEST),

    // ==========================
    // 🔹 Authentication
    // ==========================
    UNAUTHENTICATED(1201, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1202, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_PASSWORD(1203, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1204, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),

    // ==========================
    // 🔹 Validation
    // ==========================
    INVALID_DOB(1301, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1302, "Invalid email address", HttpStatus.BAD_REQUEST),
    EMAIL_IS_REQUIRED(1303, "Email is required", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1304, "Email already exists", HttpStatus.BAD_REQUEST),

    // ==========================
    // 🔹 OTP
    // ==========================
    OTP_NOT_FOUND(1401, "OTP not found", HttpStatus.NOT_FOUND),
    OTP_EXPIRED(1402, "OTP has expired", HttpStatus.BAD_REQUEST),
    OTP_INVALID(1403, "Invalid OTP code", HttpStatus.BAD_REQUEST),
    OTP_TOO_FREQUENT(1404, "Please wait before requesting another OTP", HttpStatus.TOO_MANY_REQUESTS),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
