package com.tien.postservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Khóa không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN),
    POST_EMPTY(1008, "Bài viết trống", HttpStatus.BAD_REQUEST),
    POST_NOT_FOUND(1009, "Không tìm thấy bài viết", HttpStatus.NOT_FOUND),
    POST_ALREADY_SAVED(1010, "Bài viết đã được lưu", HttpStatus.BAD_REQUEST),
    POST_NOT_SAVED(1011, "Bài viết chưa được lưu", HttpStatus.BAD_REQUEST),
    POST_NOT_OWNER(1012, "Bạn không phải là chủ sở hữu của bài viết này", HttpStatus.FORBIDDEN),
    SHARED_POST_NOT_FOUND(1013, "Không tìm thấy bài viết được chia sẻ", HttpStatus.NOT_FOUND),
    FILE_TOO_LARGE(1014, "File quá lớn. Vui lòng chọn file nhỏ hơn", HttpStatus.BAD_REQUEST),
    MAX_FILE_SIZE_EXCEEDED(1015, "Kích thước file vượt quá giới hạn cho phép", HttpStatus.BAD_REQUEST),
    MAX_REQUEST_SIZE_EXCEEDED(1016, "Kích thước request vượt quá giới hạn cho phép", HttpStatus.BAD_REQUEST),
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
