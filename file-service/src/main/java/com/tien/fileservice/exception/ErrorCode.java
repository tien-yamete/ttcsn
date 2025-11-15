package com.tien.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    FILE_NOT_FOUND(1008, "File not found", HttpStatus.NOT_FOUND),
    FILE_EMPTY(1009, "File is empty", HttpStatus.BAD_REQUEST),
    POST_ID_REQUIRED(1010, "PostId is required", HttpStatus.BAD_REQUEST),
    OWNER_ID_REQUIRED(1011, "OwnerId is required", HttpStatus.BAD_REQUEST),
    CLOUDINARY_UPLOAD_FAILED(1012, "Upload to cloudinary failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TYPE_NOT_ALLOWED(1013, "File type is not allowed", HttpStatus.BAD_REQUEST),
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
