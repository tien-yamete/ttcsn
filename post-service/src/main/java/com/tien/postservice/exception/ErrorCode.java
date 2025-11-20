package com.tien.postservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    POST_EMPTY(1008, "Post is empty", HttpStatus.BAD_REQUEST),
    POST_NOT_FOUND(1009, "Post not found", HttpStatus.NOT_FOUND),
    POST_ALREADY_SAVED(1010, "Post already saved", HttpStatus.BAD_REQUEST),
    POST_NOT_SAVED(1011, "Post not saved", HttpStatus.BAD_REQUEST),
    POST_NOT_OWNER(1012, "You are not the owner of this post", HttpStatus.FORBIDDEN),
    SHARED_POST_NOT_FOUND(1013, "Shared post not found", HttpStatus.NOT_FOUND),
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
