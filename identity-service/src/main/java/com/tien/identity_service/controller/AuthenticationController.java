package com.tien.identity_service.controller;

import com.nimbusds.jose.JOSEException;
import com.tien.identity_service.dto.ApiResponse;
import com.tien.identity_service.dto.request.AuthenticationRequest;
import com.tien.identity_service.dto.request.IntrospectRequest;
import com.tien.identity_service.dto.request.LogoutRequest;
import com.tien.identity_service.dto.request.RefreshTokenRequest;
import com.tien.identity_service.dto.response.AuthenticationResponse;
import com.tien.identity_service.dto.response.IntrospectResponse;
import com.tien.identity_service.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

// AuthenticationController: trách nhiệm xử lý các API liên quan đến xác thực và quản lý JWT:
//          - /auth/token: cấp token mới khi user đăng nhập.
//          - /auth/introspect: kiểm tra tính hợp lệ của token (introspection).
//          - /auth/refresh: cấp lại access token từ refresh token.
//          - /auth/logout: huỷ token (đăng xuất).

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshTokenRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }
}
