package com.tien.identityservice.configuration;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tien.identityservice.dto.ApiResponse;
import com.tien.identityservice.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * JwtAuthenticationEntryPoint: Xử lý khi request chưa được xác thực hoặc token không hợp lệ.
 * - Được gọi tự động bởi Spring Security khi:
 *   + Request không có token
 *   + Token không hợp lệ hoặc đã hết hạn
 * - Trả về JSON response thống nhất (thay vì HTML mặc định của Spring Security)
 * - Status code: 401 Unauthorized
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        // Ghi JSON ra response body
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        // Đảm bảo dữ liệu được flush xuống client
        response.flushBuffer();
    }
}
