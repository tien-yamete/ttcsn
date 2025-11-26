package com.tien.postservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.tien.postservice.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        var fieldError = e.getFieldError();
        if (fieldError != null && fieldError.getDefaultMessage() != null) {
            try {
                errorCode = ErrorCode.valueOf(fieldError.getDefaultMessage());
            } catch (IllegalArgumentException ignored) {
                // Use default INVALID_KEY
            }
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse> handlingMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        log.warn("Method not supported: {}", exception.getMethod());
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        return ResponseEntity.status(405)
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message("HTTP method '" + exception.getMethod() + "' is not supported")
                        .build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ApiResponse> handlingMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        log.error("File size exceeded", exception);
        ErrorCode errorCode = ErrorCode.MAX_FILE_SIZE_EXCEEDED;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(MultipartException.class)
    ResponseEntity<ApiResponse> handlingMultipartException(MultipartException exception) {
        log.error("Multipart exception", exception);
        ErrorCode errorCode = ErrorCode.MAX_REQUEST_SIZE_EXCEEDED;
        if (exception.getCause() instanceof MaxUploadSizeExceededException) {
            errorCode = ErrorCode.MAX_FILE_SIZE_EXCEEDED;
        } else if (exception.getMessage() != null
                && (exception.getMessage().contains("size") || exception.getMessage().contains("exceeded"))) {
            errorCode = ErrorCode.FILE_TOO_LARGE;
        }
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse> handlingException(Exception exception) {
        log.error("Unexpected exception", exception);
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
