package com.tien.chatservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Khóa không hợp lệ", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    CONVERSATION_NOT_FOUND(1009, "Cuộc trò chuyện không tồn tại", HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND(1010, "Tin nhắn không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_CONVERSATION_TYPE(1011, "Loại cuộc trò chuyện không hợp lệ cho thao tác này", HttpStatus.BAD_REQUEST),
    PARTICIPANT_ALREADY_EXISTS(1012, "Người tham gia đã tồn tại trong cuộc trò chuyện", HttpStatus.BAD_REQUEST),
    DUPLICATE_PARTICIPANT_IDS(1013, "Trùng lặp ID người tham gia trong yêu cầu", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1014, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1015, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING_EXPIRATION(1016, "Token thiếu thời gian hết hạn", HttpStatus.UNAUTHORIZED),
    USER_ID_REQUIRED(1017, "ID người dùng là bắt buộc", HttpStatus.BAD_REQUEST),
    EXTERNAL_SERVICE_ERROR(1018, "Lỗi kết nối với dịch vụ bên ngoài", HttpStatus.SERVICE_UNAVAILABLE),
    DATABASE_ERROR(1019, "Lỗi cơ sở dữ liệu", HttpStatus.INTERNAL_SERVER_ERROR);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
