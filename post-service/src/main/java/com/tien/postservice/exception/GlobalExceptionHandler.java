package com.tien.postservice.exception;

import java.util.Map;
import java.util.Objects;

import jakarta.validation.ConstraintViolation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tien.postservice.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

// GlobalExceptionHandler: Chịu trách nhiệm xử lý tập trung tất cả exception trong hệ thống.

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";
    //      Handler mặc định cho Exception chưa được bắt riêng.
    //            - Log stack trace.
    //            - Trả về mã lỗi UNCLASSIFIED_EXCEPTION.

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    //    Handler cho AppException (custom business exception).
    //             - Lấy ErrorCode từ exception.
    //             - Trả về status và message theo errorCode.
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    //    Handler cho AccessDeniedException (Spring Security ném ra khi không có quyền).
    //             - Trả về 401 Unauthorized.
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    //    Handler cho lỗi validation (MethodArgumentNotValidException).
    //             - Lấy error message từ @Valid annotation.
    //      - Nếu message trùng với tên ErrorCode enum -> map ra ErrorCode tương ứng.
    //      - Nếu có attributes (ví dụ min=18), thì thay thế vào message template.
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;

        var fieldError = e.getFieldError();
        if (fieldError != null) {
            String enumkey = fieldError.getDefaultMessage();
            if (enumkey != null) {
                try {
                    errorCode = ErrorCode.valueOf(enumkey);
                    var errors = e.getBindingResult().getAllErrors();
                    if (!errors.isEmpty()) {
                        try {
                            var constraintViolation = errors.get(0).unwrap(ConstraintViolation.class);
                            attributes = constraintViolation
                                    .getConstraintDescriptor()
                                    .getAttributes();
                        } catch (Exception ex) {
                            log.debug("Cannot unwrap constraint violation", ex);
                        }
                    }
                } catch (IllegalArgumentException iae) {
                    log.debug("Enum key '{}' does not match any ErrorCode", enumkey);
                }
            }
        }

        ApiResponse response = new ApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    // Thay thế placeholder trong message (ví dụ {min}) bằng giá trị thực tế
    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
