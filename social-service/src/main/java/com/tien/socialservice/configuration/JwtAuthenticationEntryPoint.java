package com.tien.socialservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tien.socialservice.dto.ApiResponse;
import com.tien.socialservice.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
            throws IOException{

        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

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
