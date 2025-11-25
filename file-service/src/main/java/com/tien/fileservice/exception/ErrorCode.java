package com.tien.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Khóa không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    FILE_NOT_FOUND(1008, "Tệp tin không tồn tại", HttpStatus.NOT_FOUND),
    FILE_EMPTY(1009, "Tệp tin trống", HttpStatus.BAD_REQUEST),
    POST_ID_REQUIRED(1010, "ID bài viết là bắt buộc", HttpStatus.BAD_REQUEST),
    OWNER_ID_REQUIRED(1011, "ID chủ sở hữu là bắt buộc", HttpStatus.BAD_REQUEST),
    CLOUDINARY_UPLOAD_FAILED(1012, "Tải lên thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TYPE_NOT_ALLOWED(1013, "Loại tệp tin không được phép", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1014, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1015, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING_EXPIRATION(1016, "Token thiếu thời gian hết hạn", HttpStatus.UNAUTHORIZED),
    FILE_TOO_LARGE(1017, "Kích thước tệp tin vượt quá giới hạn cho phép", HttpStatus.BAD_REQUEST),
    MAX_FILE_SIZE_EXCEEDED(1018, "Kích thước tệp tin vượt quá giới hạn tối đa (20MB)", HttpStatus.BAD_REQUEST),
    MAX_REQUEST_SIZE_EXCEEDED(1019, "Tổng kích thước yêu cầu vượt quá giới hạn", HttpStatus.BAD_REQUEST),
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
