package com.tien.identityservice.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tien.event.dto.NotificationEvent;
import com.tien.identityservice.constant.EmailTemplate;
import com.tien.identityservice.constant.OtpType;
import com.tien.identityservice.constant.PredefinedRole;
import com.tien.identityservice.constant.SignInProvider;
import com.tien.identityservice.dto.request.*;
import com.tien.identityservice.dto.response.AuthenticationResponse;
import com.tien.identityservice.dto.response.IntrospectResponse;
import com.tien.identityservice.dto.response.UserResponse;
import com.tien.identityservice.entity.InvalidatedToken;
import com.tien.identityservice.entity.Role;
import com.tien.identityservice.entity.User;
import com.tien.identityservice.entity.UserOtp;
import com.tien.identityservice.exception.AppException;
import com.tien.identityservice.exception.ErrorCode;
import com.tien.identityservice.mapper.ProfileMapper;
import com.tien.identityservice.mapper.UserMapper;
import com.tien.identityservice.repository.InvalidatedTokenRepository;
import com.tien.identityservice.repository.RoleRepository;
import com.tien.identityservice.repository.UserOtpRepository;
import com.tien.identityservice.repository.UserRepository;
import com.tien.identityservice.repository.httpclient.ProfileClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {
    UserRepository userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;

    ProfileClient profileClient;

    ProfileMapper profileMapper;

    KafkaTemplate<String, Object> kafkaTemplate;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    RoleRepository roleRepository;

    UserOtpRepository userOtpRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNED_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.issuer}")
    protected String ISSUER;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

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

        var profileRequest = profileMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(user.getId());

        var profile = profileClient.createProfile(profileRequest);

        String otpCode = generateVerificationCode();

        UserOtp userOtp = UserOtp.builder()
                .user(user)
                .otpCode(otpCode)
                .type(OtpType.REGISTER)
                .expiryTime(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        userOtpRepository.save(userOtp);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(request.getEmail())
                .subject("Verify email")
                .body(EmailTemplate.otpEmail(request.getUsername(), otpCode))
                .build();

        // Publish message to kafka
        kafkaTemplate.send("notification-delivery", notificationEvent);

        var userCreationReponse = userMapper.toUserResponse(user);
        userCreationReponse.setId(profile.getResult().getId());

        return userCreationReponse;
    }

    @Transactional
    public void verifyUser(VerifyUserRequest verifyUserRequest) {
        User user = userRepository
                .findByEmail(verifyUserRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserOtp userOtp = userOtpRepository
                .findTopByUserAndTypeAndUsedFalseOrderByCreatedAtDesc(user, OtpType.REGISTER)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));

        if (userOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        if (!userOtp.getOtpCode().equals(verifyUserRequest.getOtpCode())) {
            userOtp.setUsed(true); // Đánh dấu OTP sai là đã dùng
            userOtpRepository.save(userOtp);
            throw new AppException(ErrorCode.OTP_INVALID);
        }

        user.setEmailVerified(true);
        user.setIsActive(true);
        userRepository.save(user);

        userOtp.setUsed(true);
        userOtpRepository.save(userOtp);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(verifyUserRequest.getEmail())
                .subject("Welcome to Friendify")
                .body(EmailTemplate.welcomeEmail(user.getUsername()))
                .build();

        // Publish message to kafka
        kafkaTemplate.send("notification-delivery", notificationEvent);
    }

    @Transactional
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (user.getIsActive() && user.isEmailVerified()) {
            throw new AppException(ErrorCode.USER_ALREADY_VERIFIED);
        }

        Optional<UserOtp> lastOtp =
                userOtpRepository.findTopByUserAndTypeAndUsedFalseOrderByCreatedAtDesc(user, OtpType.REGISTER);
        if (lastOtp.isPresent()
                && lastOtp.get().getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(60))) {
            throw new AppException(ErrorCode.OTP_TOO_FREQUENT);
        }

        userOtpRepository.deactivateOldOtp(user.getId(), OtpType.REGISTER);

        String otpCode = generateVerificationCode();

        UserOtp newOtp = UserOtp.builder()
                .user(user)
                .otpCode(otpCode)
                .type(OtpType.REGISTER)
                .expiryTime(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        userOtpRepository.save(newOtp);
        NotificationEvent otpMail = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(user.getEmail())
                .subject("New verification code")
                .body(EmailTemplate.resendVerificationEmail(user.getUsername(), otpCode))
                .build();

        kafkaTemplate.send("notification-delivery", otpMail);
    }
    //    Introspect: kiểm tra token có hợp lệ hay không.
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    // Xác thực username/password và phát access token.
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

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    //    Logout: revoke token hiện tại (lưu vào bảng invalidated_token).
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            // Token hết hạn hoặc không hợp lệ -> coi như đã "logout" rồi
            log.info("Token already expired");
        }
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException {
        var signJWT = verifyToken(request.getToken(), true);

        var jit = signJWT.getJWTClaimsSet().getJWTID();

        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        // Revoke token cũ
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        // Lấy user từ subject để phát token mới
        var userId = signJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByIdWithRolesAndPermissions(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNED_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        // Chưa verify hoặc đã hết hạn theo rule tương ứng -> UNAUTHENTICATED
        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        // Nếu token đã bị revoke (có trong bảng invalidated_token) -> UNAUTHENTICATED
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    @Transactional(readOnly = true)
    public String generateToken(User user) {
        // Ensure roles and permissions are loaded
        User userWithRoles = userRepository.findByIdWithRolesAndPermissions(user.getId())
                .orElse(user);
        
        // Initialize lazy collections if needed
        if (userWithRoles.getRoles() != null) {
            userWithRoles.getRoles().size(); // Force initialization
            userWithRoles.getRoles().forEach(role -> {
                if (role.getPermissions() != null) {
                    role.getPermissions().size(); // Force initialization
                }
            });
        }

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userWithRoles.getId()) // them userId vao token
                .issuer(ISSUER)
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(userWithRoles)) // scope dạng "ROLE_X READ_USER WRITE_USER ..."
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNED_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    @Transactional
    public User createUserFromGoogleOAuth(String email, String name, String providerUserId) {
        // Check if user already exists by email
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update provider info if not set
            if (user.getProvider() == null || user.getProvider() != SignInProvider.GOOGLE) {
                user.setProvider(SignInProvider.GOOGLE);
                user.setProviderUserId(providerUserId);
                user.setEmailVerified(true);
                user.setIsActive(true);
                user = userRepository.save(user);
            }
            return user;
        }

        // Create new user from Google OAuth
        String[] nameParts = name != null && !name.isEmpty() ? name.split(" ", 2) : new String[] {"", ""};
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        String username = email.split("@")[0] + "_" + System.currentTimeMillis();

        User user = User.builder()
                .email(email)
                .username(username)
                .provider(SignInProvider.GOOGLE)
                .providerUserId(providerUserId)
                .emailVerified(true)
                .isActive(true)
                .build();

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            // If username conflict, try again with different username
            username = email.split("@")[0] + "_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
            user.setUsername(username);
            user = userRepository.save(user);
        }

        // Create profile for the user
        ProfileCreationRequest profileRequest = ProfileCreationRequest.builder()
                .userId(user.getId())
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        profileClient.createProfile(profileRequest);

        return user;
    }
}
