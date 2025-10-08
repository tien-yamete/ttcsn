package com.tien.identityservice.controller;

import java.text.ParseException;

import com.tien.identityservice.dto.request.*;
import com.tien.identityservice.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.JOSEException;
import com.tien.identityservice.dto.ApiResponse;
import com.tien.identityservice.dto.response.AuthenticationResponse;
import com.tien.identityservice.dto.response.IntrospectResponse;
import com.tien.identityservice.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/registration")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Controller: Create user");
        return ApiResponse.<UserResponse>builder()
                .message("User registered successfully. Please verify your email.")
                .result(authenticationService.register(request))
                .build();
    }

    @PostMapping("/verify-user")
    public ApiResponse<Void> verifyUser(@RequestBody VerifyUserRequest request) {
        log.info("Verifying OTP for email: {}", request.getEmail());
        authenticationService.verifyUser(request);
        return ApiResponse.<Void>builder()
                .message("Email verified successfully. You can now login.")
                .build();
    }

    @PostMapping("/resend-verification")
    public ApiResponse<Void> resendVerificationCode(@RequestBody ResendOtpRequest request) {
        log.info("Resending OTP for email: {}", request.getEmail());
        authenticationService.resendVerificationCode(request.getEmail());
        return ApiResponse.<Void>builder()
                .message("A new verification code has been sent to your email.")
                .build();
    }

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
