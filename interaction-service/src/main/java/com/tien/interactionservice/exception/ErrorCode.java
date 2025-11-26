package com.tien.interactionservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Khóa không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN),
    POST_NOT_FOUND(1009, "Không tìm thấy bài viết", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND(2001, "Không tìm thấy bình luận", HttpStatus.NOT_FOUND),
    INVALID_PARENT_COMMENT(2002, "Bình luận cha không hợp lệ", HttpStatus.BAD_REQUEST),
    LIKE_NOT_FOUND(2003, "Không tìm thấy lượt thích", HttpStatus.NOT_FOUND),
    ALREADY_LIKED(2004, "Đã thích rồi", HttpStatus.BAD_REQUEST),
    INVALID_LIKE_REQUEST(2005, "Yêu cầu thích không hợp lệ", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(2006, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
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

