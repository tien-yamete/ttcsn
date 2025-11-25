package com.tien.socialservice.configuration;

import java.text.ParseException;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;

/**
 * CustomJwtDecoder: Giải mã JWT token thủ công.
 * - Parse claims, header, thời gian hiệu lực từ token
 * - Sử dụng Nimbus JOSE để parse và convert sang Spring Security Jwt object
 * - Được sử dụng bởi SecurityConfig để xác thực JWT token
 */
@Slf4j
@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            return new Jwt(
                    token,
                    signedJWT.getJWTClaimsSet().getIssueTime().toInstant(),
                    signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(),
                    signedJWT.getHeader().toJSONObject(),
                    signedJWT.getJWTClaimsSet().getClaims());

        } catch (ParseException e) {
            log.error("Failed to parse JWT token", e);
            throw new JwtException("Invalid token", e);
        }
    }
}
