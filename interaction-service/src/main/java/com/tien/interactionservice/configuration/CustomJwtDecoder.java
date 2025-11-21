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
                throw new JwtException("Token missing expiration time");
            }

            return new Jwt(
                    token,
                    issuedAt,
                    expirationTime.toInstant(),
                    signedJWT.getHeader().toJSONObject(),
                    claimsSet.getClaims());
        } catch (ParseException exception) {
            throw new JwtException("Invalid Token: " + exception.getMessage());
        } catch (Exception exception) {
            throw new JwtException("Failed to decode token: " + exception.getMessage());
        }
    }
}

