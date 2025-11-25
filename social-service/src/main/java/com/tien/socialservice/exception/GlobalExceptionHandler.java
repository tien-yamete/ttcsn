package com.tien.socialservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tien.socialservice.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

// GlobalExceptionHandler: Chịu trách nhiệm xử lý tập trung tất cả exception trong hệ thống.
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Handler cho AppException (custom business exception).
    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse<?>> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // Handler cho AccessDeniedException (Spring Security ném ra khi không có quyền).
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode())
                .body(ApiResponse.builder()
                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                        .build());
    }
}
