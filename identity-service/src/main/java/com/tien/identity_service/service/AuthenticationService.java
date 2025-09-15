package com.tien.identity_service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tien.identity_service.dto.request.AuthenticationRequest;
import com.tien.identity_service.dto.request.IntrospectRequest;
import com.tien.identity_service.dto.request.LogoutRequest;
import com.tien.identity_service.dto.request.RefreshTokenRequest;
import com.tien.identity_service.dto.response.AuthenticationResponse;
import com.tien.identity_service.dto.response.IntrospectResponse;
import com.tien.identity_service.entity.InvalidatedToken;
import com.tien.identity_service.entity.User;
import com.tien.identity_service.exception.AppException;
import com.tien.identity_service.exception.ErrorCode;
import com.tien.identity_service.repository.InvalidatedTokenRepository;
import com.tien.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {
    UserRepository userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;

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

    //    Introspect: kiểm tra token có hợp lệ hay không.
    //             - Không ném lỗi ra ngoài, chỉ trả về cờ true/false.
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
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    //    Logout: revoke token hiện tại (lưu vào bảng invalidated_token).
    //             - Nếu token đã hết hạn thì bỏ qua (đã không còn giá trị).
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

    //    Refresh token:
    //             - Verify token theo mode refresh (dựa trên issueTime + REFRESHABLE_DURATION).
    //             - Revoke token cũ.
    //             - Phát access token mới cho user tương ứng.
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException {
        var signJWT = verifyToken(request.getToken(), true);

        var jit = signJWT.getJWTClaimsSet().getJWTID();

        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        // Revoke token cũ
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        // Lấy user từ subject để phát token mới
        var username = signJWT.getJWTClaimsSet().getSubject();

        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    //    Verify token:
    //             - Kiểm tra chữ ký HMAC.
    //             - Kiểm tra hạn (access: exp; refresh: iat + REFRESHABLE_DURATION).
    //             - Kiểm tra token đã bị revoke chưa.
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

    // Tạo token
    //    Phát access token HS512.
    //             - subject  : username của user.
    //             - issuer   : ISSUER.
    //             - iat/exp  : issue/expire theo VALID_DURATION.
    //             - jti      : random UUID, hỗ trợ revoke.
    //             - scope    : ghép từ role/permission (dạng chuỗi cách nhau bởi space).
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer(ISSUER)
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user)) // scope dạng "ROLE_X READ_USER WRITE_USER ..."
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

    // Tạo chuỗi scope từ roles và permissions của user
    //             - Mỗi role tạo 1 entry "ROLE_{roleName}".
    //             - Mỗi permission của role thêm đúng tên permission.
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
}
