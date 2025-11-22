package com.tien.identityservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Key không hợp lệ", HttpStatus.BAD_REQUEST),

    USER_EXISTED(1101, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1102, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    USER_DISABLED(1103, "Tài khoản người dùng đã bị vô hiệu hóa", HttpStatus.FORBIDDEN),
    USER_ALREADY_VERIFIED(1104, "Người dùng đã được xác minh", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(1201, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1202, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    INVALID_PASSWORD(1203, "Mật khẩu phải có ít nhất {min} ký tự", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1204, "Tên người dùng phải có ít nhất {min} ký tự", HttpStatus.BAD_REQUEST),
    INVALID_OLD_PASSWORD(1205, "Mật khẩu cũ không đúng", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(1206, "Mật khẩu không đúng", HttpStatus.BAD_REQUEST),

    INVALID_DOB(1301, "Tuổi của bạn phải ít nhất {min}", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1302, "Địa chỉ email không hợp lệ", HttpStatus.BAD_REQUEST),
    EMAIL_IS_REQUIRED(1303, "Email là bắt buộc", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1304, "Email đã tồn tại", HttpStatus.BAD_REQUEST),

    OTP_NOT_FOUND(1401, "Không tìm thấy mã OTP", HttpStatus.NOT_FOUND),
    OTP_EXPIRED(1402, "Mã OTP đã hết hạn", HttpStatus.BAD_REQUEST),
    OTP_INVALID(1403, "Mã OTP không hợp lệ", HttpStatus.BAD_REQUEST),
    OTP_TOO_FREQUENT(1404, "Vui lòng chờ trước khi yêu cầu mã OTP tiếp theo", HttpStatus.TOO_MANY_REQUESTS),
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
