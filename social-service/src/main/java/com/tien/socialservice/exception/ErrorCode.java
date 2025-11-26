package com.tien.socialservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_EXISTED(1001, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    FOLLOW_ALREADY_EXISTS(2001, "Đã theo dõi người dùng này", HttpStatus.BAD_REQUEST),
    FOLLOW_NOT_FOUND(2002, "Không tìm thấy mối quan hệ theo dõi", HttpStatus.NOT_FOUND),
    CANNOT_FOLLOW_SELF(2003, "Không thể theo dõi chính mình", HttpStatus.BAD_REQUEST),
    FRIENDSHIP_ALREADY_EXISTS(3001, "Tình bạn đã tồn tại", HttpStatus.BAD_REQUEST),
    FRIENDSHIP_NOT_FOUND(3002, "Không tìm thấy tình bạn", HttpStatus.NOT_FOUND),
    CANNOT_FRIEND_SELF(3003, "Không thể gửi lời mời kết bạn cho chính mình", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_ALREADY_SENT(3004, "Đã gửi lời mời kết bạn", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_NOT_PENDING(3005, "Lời mời kết bạn không ở trạng thái chờ", HttpStatus.BAD_REQUEST),
    USER_ALREADY_BLOCKED(4001, "Người dùng đã bị chặn", HttpStatus.BAD_REQUEST),
    USER_NOT_BLOCKED(4002, "Người dùng chưa bị chặn", HttpStatus.NOT_FOUND),
    CANNOT_BLOCK_SELF(4003, "Không thể chặn chính mình", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEYWORD(1002, "Từ khóa không hợp lệ", HttpStatus.BAD_REQUEST),
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
