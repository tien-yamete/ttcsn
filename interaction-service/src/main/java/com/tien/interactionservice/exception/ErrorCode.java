package com.tien.interactionservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    POST_NOT_FOUND(1009, "Post not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND(2001, "Comment not found", HttpStatus.NOT_FOUND),
    INVALID_PARENT_COMMENT(2002, "Invalid parent comment", HttpStatus.BAD_REQUEST),
    LIKE_NOT_FOUND(2003, "Like not found", HttpStatus.NOT_FOUND),
    ALREADY_LIKED(2004, "Already liked", HttpStatus.BAD_REQUEST),
    INVALID_LIKE_REQUEST(2005, "Invalid like request. Must provide either postId or commentId", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(2006, "User not found", HttpStatus.NOT_FOUND),
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

