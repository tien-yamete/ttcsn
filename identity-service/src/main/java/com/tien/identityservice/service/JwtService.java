package com.tien.identityservice.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tien.identityservice.entity.InvalidatedToken;
import com.tien.identityservice.entity.Permission;
import com.tien.identityservice.entity.User;
import com.tien.identityservice.exception.AppException;
import com.tien.identityservice.exception.ErrorCode;
import com.tien.identityservice.repository.InvalidatedTokenRepository;
import com.tien.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

/**
 * JwtService: Service xử lý JWT token với các tính năng:
 * - generateToken: Tạo JWT token từ thông tin user (bao gồm roles và permissions)
 * - verifyToken: Xác thực token (kiểm tra chữ ký, thời gian hết hạn, token đã bị revoke chưa)
 * - isValidToken: Kiểm tra token có hợp lệ không (wrapper của verifyToken)
 * - revokeToken: Vô hiệu hóa token (lưu vào bảng invalidated_token)
 * - revokeTokenById: Vô hiệu hóa token theo JWT ID (dùng khi refresh token)
 * - buildScope: Xây dựng scope string từ roles và permissions của user
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService {

    private static final String SCOPE_CLAIM = "scope";
    private static final String ROLE_PREFIX = "ROLE_";

    UserRepository userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    String signerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    long validDuration;

    @NonFinal
    @Value("${jwt.issuer}")
    String issuer;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    long refreshableDuration;

    // Tạo JWT token từ thông tin user.
    public String generateToken(User user) {
        // Load user với roles và permissions từ database.
        User userWithRoles =
                userRepository.findByIdWithRolesAndPermissions(user.getId()).orElse(user);

        initializeLazyCollections(userWithRoles);

        JWTClaimsSet claimsSet = buildJwtClaimsSet(userWithRoles);

        JWSObject jwsObject = createSignedJwsObject(claimsSet);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Không thể tạo JWT token cho user: {}", user.getId(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    // Xác thực JWT token
    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        boolean isVerified = signedJWT.verify(verifier);
        Date expiryTime = calculateExpiryTime(signedJWT, isRefresh);
        boolean isExpired = !expiryTime.after(new Date());
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        boolean isRevoked = invalidatedTokenRepository.existsById(jwtId);

        if (!isVerified || isExpired) {
            log.warn("Token không hợp lệ hoặc đã hết hạn. JWT ID: {}", jwtId);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (isRevoked) {
            log.warn("Token đã bị revoke. JWT ID: {}", jwtId);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    // Xây dựng scope string từ roles và permissions của user.
    private String buildScope(User user) {
        if (CollectionUtils.isEmpty(user.getRoles())) {
            return "";
        }

        return user.getRoles().stream()
                .flatMap(role -> {
                    String roleScope = ROLE_PREFIX + role.getName();
                    if (CollectionUtils.isEmpty(role.getPermissions())) {
                        return java.util.stream.Stream.of(roleScope);
                    }
                    return java.util.stream.Stream.concat(
                            java.util.stream.Stream.of(roleScope),
                            role.getPermissions().stream().map(Permission::getName));
                })
                .collect(Collectors.joining(" "));
    }

    // Khởi tạo lazy collections để tránh LazyInitializationException.
    private void initializeLazyCollections(User user) {
        if (user.getRoles() != null) {
            user.getRoles().size(); // Force initialization
            user.getRoles().forEach(role -> {
                if (role.getPermissions() != null) {
                    role.getPermissions().size(); // Force initialization
                }
            });
        }
    }

    // Tạo JWT Claims Set từ thông tin user.
    private JWTClaimsSet buildJwtClaimsSet(User user) {
        Date now = new Date();
        Date expirationTime =
                new Date(Instant.now().plus(validDuration, ChronoUnit.SECONDS).toEpochMilli());

        return new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer(issuer)
                .issueTime(now)
                .expirationTime(expirationTime)
                .jwtID(UUID.randomUUID().toString())
                .claim(SCOPE_CLAIM, buildScope(user))
                .build();
    }

    // Tạo JWS Object từ claims set.
    private JWSObject createSignedJwsObject(JWTClaimsSet claimsSet) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        Payload payload = new Payload(claimsSet.toJSONObject());
        return new JWSObject(header, payload);
    }

    /**
     * Tính toán thời gian hết hạn của token.
     * - Nếu là refresh token: dùng refreshableDuration
     * - Nếu là access token: dùng expirationTime từ token
     */
    private Date calculateExpiryTime(SignedJWT signedJWT, boolean isRefresh) throws ParseException {
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        if (isRefresh) {
            Date issueTime = claimsSet.getIssueTime();
            if (issueTime == null) {
                log.warn("Token không có issueTime, không thể tính refresh expiry");
                return null;
            }
            return new Date(issueTime
                    .toInstant()
                    .plus(refreshableDuration, ChronoUnit.SECONDS)
                    .toEpochMilli());
        }

        return claimsSet.getExpirationTime();
    }

    // Kiểm tra token có hợp lệ không
    public boolean isValidToken(String token) {
        try {
            verifyToken(token, false);
            return true;
        } catch (Exception e) {
            log.debug("Token không hợp lệ: {}", e.getMessage());
            return false;
        }
    }

    // Vô hiệu hóa token bằng cách lưu vào bảng invalidated_token.
    // Token đã bị revoke sẽ không thể sử dụng được nữa (ngay cả khi chưa hết hạn).
    @Transactional
    public void revokeToken(String token) {
        try {
            SignedJWT signedJWT = verifyToken(token, true);
            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
            log.debug("Đã revoke token thành công. JWT ID: {}", jit);
        } catch (Exception exception) {
            // Token hết hạn hoặc không hợp lệ -> coi như đã "logout" rồi
            log.info("Token đã hết hạn hoặc không hợp lệ, không cần revoke");
        }
    }

    // Vô hiệu hóa token theo JWT ID (jit) - dùng khi refresh token.
    @Transactional
    public void revokeTokenById(String jit, Date expiryTime) {
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);
        log.debug("Đã revoke token theo ID thành công. JWT ID: {}", jit);
    }
}
