package com.tien.identityservice.service;

import java.text.ParseException;
import java.util.HashSet;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.tien.identityservice.constant.EmailTemplate;
import com.tien.identityservice.constant.OtpType;
import com.tien.identityservice.constant.PredefinedRole;
import com.tien.identityservice.dto.request.*;
import com.tien.identityservice.dto.response.AuthenticationResponse;
import com.tien.identityservice.dto.response.IntrospectResponse;
import com.tien.identityservice.dto.response.UserResponse;
import com.tien.identityservice.entity.Role;
import com.tien.identityservice.entity.User;
import com.tien.identityservice.entity.UserOtp;
import com.tien.identityservice.exception.AppException;
import com.tien.identityservice.exception.ErrorCode;
import com.tien.identityservice.mapper.UserMapper;
import com.tien.identityservice.repository.RoleRepository;
import com.tien.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// AuthenticationService: Service xử lý nghiệp vụ liên quan đến xác thực.
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {
    UserRepository userRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    RoleRepository roleRepository;

    JwtService jwtService;

    OtpService otpService;

    NotificationService notificationService;

    ProfileService profileService;

    // Đăng ký user mới, tạo OTP, gửi email xác thực
    @Transactional
    public UserResponse register(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        user.setEmailVerified(false);
        user.setIsActive(false);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Tạo profile cho user
        var profile = profileService.createProfileFromCreation(request, user.getId());

        var userOtp = otpService.createOtp(user, OtpType.REGISTER, 15);

        // Gửi email xác thực
        notificationService.sendEmail(
                request.getEmail(),
                "Verify email",
                EmailTemplate.otpEmail(request.getUsername(), userOtp.getOtpCode()));

        var userCreationReponse = userMapper.toUserResponse(user);
        userCreationReponse.setId(user.getId());

        return userCreationReponse;
    }

    // Xác thực email bằng OTP, kích hoạt tài khoản
    @Transactional
    public void verifyUser(VerifyUserRequest verifyUserRequest) {
        User user = userRepository
                .findByEmail(verifyUserRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserOtp userOtp = otpService.findLatestOtp(user, OtpType.REGISTER);
        otpService.validateOtp(userOtp, verifyUserRequest.getOtpCode());

        user.setEmailVerified(true);
        user.setIsActive(true);
        userRepository.save(user);

        otpService.markOtpAsUsed(userOtp);

        notificationService.sendEmail(
                verifyUserRequest.getEmail(), "Welcome to Friendify", EmailTemplate.welcomeEmail(user.getUsername()));
    }

    // Gửi lại mã OTP xác thực
    @Transactional
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (user.getIsActive() && user.isEmailVerified()) {
            throw new AppException(ErrorCode.USER_ALREADY_VERIFIED);
        }

        otpService.checkOtpFrequency(user, OtpType.REGISTER);
        otpService.deactivateOldOtps(user.getId(), OtpType.REGISTER);

        UserOtp newOtp = otpService.createOtp(user, OtpType.REGISTER, 15);
        notificationService.sendEmail(
                user.getEmail(),
                "New verification code",
                EmailTemplate.resendVerificationEmail(user.getUsername(), newOtp.getOtpCode()));
    }

    // Kiểm tra token có hợp lệ hay không.
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = jwtService.isValidToken(token);
        return IntrospectResponse.builder().valid(isValid).build();
    }

    // Xác thực username/password, trả về JWT token.
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository
                .findByUsernameWithRolesAndPermissions(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (!user.getIsActive()) {
            throw new AppException(ErrorCode.USER_DISABLED);
        }

        var token = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    // Revoke token (đánh dấu token không còn hợp lệ)
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        jwtService.revokeToken(request.getToken());
    }

    // Làm mới token (revoke token cũ, tạo token mới)
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException {
        var signedJWT = jwtService.verifyToken(request.getToken(), true);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        var jit = claimsSet.getJWTID();
        var expiryTime = claimsSet.getExpirationTime();

        if (jit == null || expiryTime == null) {
            log.warn("Token không có JWT ID hoặc expiry time");
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Revoke token cũ
        jwtService.revokeTokenById(jit, expiryTime);

        // Lấy user từ subject để phát token mới
        var userId = claimsSet.getSubject();
        if (userId == null || userId.trim().isEmpty()) {
            log.warn("Token không có subject (userId)");
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var user = userRepository
                .findByIdWithRolesAndPermissions(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }
}
