package com.wanderly.authservice.service;

import com.wanderly.authservice.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    private static final int ACCESS_TOKEN_EXPIRATION = 15; // 15 minutes
    private static final int REFRESH_TOKEN_EXPIRATION = 60 * 24 * 7; // 7 days


    public String generateToken(UUID userId, TokenType tokenType) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("wanderly")
                .issuedAt(now)
                .expiresAt(now.plus(tokenType.equals(TokenType.ACCESS) ? ACCESS_TOKEN_EXPIRATION : REFRESH_TOKEN_EXPIRATION, ChronoUnit.MINUTES))
                .subject(String.valueOf(userId))
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public UUID extractUserId(String token) {
        String sub = extractClaim(token, "sub", String.class);
        return UUID.fromString(sub);
    }

    public Instant extractIssuedAt(String token) {
        return extractClaim(token, "iat", Instant.class);
    }


    public boolean isTokenExpired(String token) {
        Instant expiration = extractClaim(token, "exp", Instant.class);
        return expiration.toEpochMilli() < System.currentTimeMillis();
    }

    private <T> T extractClaim(String token, String claim, Class<T> clazz) {
        if (token.contains("Bearer")) {
            token = token.substring(7);
        }
        Jwt jwt = decoder.decode(token);
        return jwt.getClaim(claim);
    }
}
