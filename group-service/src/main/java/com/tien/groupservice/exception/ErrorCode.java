package com.tien.groupservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
	UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_KEY(1001, "Khóa không hợp lệ", HttpStatus.BAD_REQUEST),
	UNAUTHENTICATED(1006, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
	UNAUTHORIZED(1007, "Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN),
	GROUP_NOT_FOUND(2001, "Không tìm thấy nhóm", HttpStatus.NOT_FOUND),
	GROUP_ALREADY_EXISTS(2002, "Nhóm đã tồn tại", HttpStatus.BAD_REQUEST),
	GROUP_NOT_OWNER(2003, "Bạn không phải là chủ sở hữu của nhóm này", HttpStatus.FORBIDDEN),
	GROUP_NAME_REQUIRED(2004, "Tên nhóm là bắt buộc", HttpStatus.BAD_REQUEST),
	MEMBER_NOT_FOUND(2005, "Không tìm thấy thành viên trong nhóm", HttpStatus.NOT_FOUND),
	MEMBER_ALREADY_EXISTS(2006, "Thành viên đã tồn tại trong nhóm", HttpStatus.BAD_REQUEST),
	MEMBER_CANNOT_REMOVE_OWNER(2007, "Không thể xóa chủ sở hữu nhóm", HttpStatus.BAD_REQUEST),
	INVALID_ROLE(2008, "Vai trò thành viên không hợp lệ", HttpStatus.BAD_REQUEST),
	CANNOT_CHANGE_OWNER_ROLE(2009, "Không thể thay đổi vai trò của chủ sở hữu", HttpStatus.BAD_REQUEST),
	JOIN_REQUEST_NOT_FOUND(2010, "Không tìm thấy yêu cầu tham gia", HttpStatus.NOT_FOUND),
	JOIN_REQUEST_ALREADY_EXISTS(2011, "Yêu cầu tham gia đã tồn tại", HttpStatus.BAD_REQUEST),
	ALREADY_MEMBER(2012, "Người dùng đã là thành viên của nhóm này", HttpStatus.BAD_REQUEST),
	INSUFFICIENT_PERMISSION(2013, "Bạn không có đủ quyền để thực hiện hành động này", HttpStatus.FORBIDDEN),
	CANNOT_JOIN_GROUP(2014, "Không thể tham gia nhóm này", HttpStatus.BAD_REQUEST),
	POSTING_NOT_ALLOWED(2015, "Không được phép đăng bài trong nhóm này", HttpStatus.FORBIDDEN),
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

