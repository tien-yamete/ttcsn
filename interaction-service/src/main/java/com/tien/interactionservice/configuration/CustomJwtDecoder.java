package com.tien.interactionservice.configuration;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            var claimsSet = signedJWT.getJWTClaimsSet();

            var issuedAt = claimsSet.getIssueTime() != null
                    ? claimsSet.getIssueTime().toInstant()
                    : Instant.now();

            var expirationTime = claimsSet.getExpirationTime();
            if (expirationTime == null) {
                throw new JwtException("Token thiếu thời gian hết hạn");
            }

            return new Jwt(
                    token,
                    issuedAt,
                    expirationTime.toInstant(),
                    signedJWT.getHeader().toJSONObject(),
                    claimsSet.getClaims());
        } catch (ParseException exception) {
            throw new JwtException("Token không hợp lệ: " + exception.getMessage());
        } catch (Exception exception) {
            throw new JwtException("Không thể giải mã token: " + exception.getMessage());
        }
    }
}

