package com.tien.chatservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    CONVERSATION_NOT_FOUND(1009, "Chat conversation not found", HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND(1010, "Message not found", HttpStatus.NOT_FOUND),
    INVALID_CONVERSATION_TYPE(1011, "Invalid conversation type for this operation", HttpStatus.BAD_REQUEST),
    PARTICIPANT_ALREADY_EXISTS(1012, "Participant already exists in conversation", HttpStatus.BAD_REQUEST),
    DUPLICATE_PARTICIPANT_IDS(1013, "Duplicate participant IDs in request", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
