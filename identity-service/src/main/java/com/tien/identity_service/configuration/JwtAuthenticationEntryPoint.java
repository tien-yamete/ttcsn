package com.tien.identity_service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tien.identity_service.dto.ApiResponse;
import com.tien.identity_service.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

// JwtAuthenticationEntryPoint:
//         - Được gọi khi có request chưa được xác thực hoặc token không hợp lệ.
//         - Trả về JSON thống nhất thay vì HTML mặc định của Spring Security.

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // Lấy mã lỗi UNAUTHORIZED (401) từ enum ErrorCode
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        // Convert object -> JSON string
        ObjectMapper objectMapper = new ObjectMapper();

        // Ghi JSON ra response body
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        // Đảm bảo dữ liệu được flush xuống client
        response.flushBuffer();
    }
}
