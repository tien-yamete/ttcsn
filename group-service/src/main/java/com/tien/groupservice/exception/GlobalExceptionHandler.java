package com.tien.groupservice.exception;

import com.tien.groupservice.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(value = AppException.class)
	ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		ApiResponse<?> apiResponse = ApiResponse.builder()
				.code(errorCode.getCode())
				.message(errorCode.getMessage())
				.result(null)
				.build();

		log.error("Exception: {}", errorCode.getMessage());
		return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
	}
}

