package com.tien.identityservice.controller;

import java.text.ParseException;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.JOSEException;
import com.tien.identityservice.dto.ApiResponse;
import com.tien.identityservice.dto.request.*;
import com.tien.identityservice.dto.response.AuthenticationResponse;
import com.tien.identityservice.dto.response.IntrospectResponse;
import com.tien.identityservice.dto.response.UserResponse;
import com.tien.identityservice.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthenticationController: Controller xử lý các API liên quan đến xác thực và đăng ký.
 * - POST /auth/registration: Đăng ký user mới
 * - POST /auth/verify-user: Xác thực email bằng OTP
 * - POST /auth/resend-verification: Gửi lại mã OTP
 * - POST /auth/token: Đăng nhập (username/password) và nhận JWT token
 * - POST /auth/introspect: Kiểm tra token có hợp lệ không
 * - POST /auth/refresh: Làm mới token
 * - POST /auth/logout: Đăng xuất (revoke token)
 * - POST /auth/forgot-password: Quên mật khẩu (gửi OTP)
 * - POST /auth/reset-password: Reset mật khẩu với OTP
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/registration")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(authenticationService.register(request))
                .build();
    }

    @PostMapping("/verify-user")
    public ApiResponse<Void> verifyUser(@RequestBody VerifyUserRequest request) {
        log.info("Đang xác thực OTP cho email: {}", request.getEmail());
        authenticationService.verifyUser(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/resend-verification")
    public ApiResponse<Void> resendVerificationCode(@RequestBody ResendOtpRequest request) {
        authenticationService.resendVerificationCode(request.getEmail());
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        return ApiResponse.<IntrospectResponse>builder()
                .result(authenticationService.introspect(request))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshTokenRequest request)
            throws ParseException, JOSEException {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.refreshToken(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .build();
    }
}
