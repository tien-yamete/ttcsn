package com.tien.socialservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    USER_NOT_EXISTED(1001, "User not existed", HttpStatus.NOT_FOUND),
    FOLLOW_ALREADY_EXISTS(2001, "Already following this user", HttpStatus.BAD_REQUEST),
    FOLLOW_NOT_FOUND(2002, "Follow relationship not found", HttpStatus.NOT_FOUND),
    CANNOT_FOLLOW_SELF(2003, "Cannot follow yourself", HttpStatus.BAD_REQUEST),
    FRIENDSHIP_ALREADY_EXISTS(3001, "Friendship already exists", HttpStatus.BAD_REQUEST),
    FRIENDSHIP_NOT_FOUND(3002, "Friendship not found", HttpStatus.NOT_FOUND),
    CANNOT_FRIEND_SELF(3003, "Cannot send friend request to yourself", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_ALREADY_SENT(3004, "Friend request already sent", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_NOT_PENDING(3005, "Friend request is not pending", HttpStatus.BAD_REQUEST),
    USER_ALREADY_BLOCKED(4001, "User already blocked", HttpStatus.BAD_REQUEST),
    USER_NOT_BLOCKED(4002, "User not blocked", HttpStatus.NOT_FOUND),
    CANNOT_BLOCK_SELF(4003, "Cannot block yourself", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
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
