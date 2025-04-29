package com.wanderly.authservice.service;

import com.wanderly.authservice.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    private static final long ACCESS_TOKEN_EXPIRATION = 15; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 60 * 24 * 7; // 7 days


    public String generateToken(String email, TokenType tokenType) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("wanderly")
                .issuedAt(now)
                .expiresAt(now.plus(tokenType.equals(TokenType.ACCESS) ? ACCESS_TOKEN_EXPIRATION : REFRESH_TOKEN_EXPIRATION, ChronoUnit.MINUTES))
                .subject(email)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

//    private String buildToken(String subject, boolean accessToken) {
//        Account account = accountService.findByEmail(subject);
//
//        long expiration = accessToken ? ACCESS_TOKEN_EXPIRATION : REFRESH_TOKEN_EXPIRATION;
//        List<String> scope = account.getRoles().stream().map(GrantedAuthority::getAuthority).toList();
//
//        Map<String, Object> payload = new HashMap<>();
//        if (accessToken) {
//            AccountCompany accountCompany = accountCompanyService.findByAccountEmail(subject)
//                    .orElse(null);
//            boolean hasCompany = accountCompany != null && accountCompany.getInvited().equals(CompanyInvite.accepted);
//            boolean isCompanyAdmin = accountCompany != null && accountCompany.getCompanyAdmin();
//            payload.put("name", account.getFullName());
//            payload.put("img", s3Adapter.retrieveFile("profile", account.getProfile().getProfilePhoto()));
//            payload.put("steps", account.hasEmptySteps());
//            payload.put("url", account.getUniqueUrl());
//            payload.put("company", hasCompany);
//            payload.put("companyAdmin", isCompanyAdmin);
//        }
//
//        Instant now = Instant.now();
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuer("cnnect")
//                .issuedAt(now)
//                .expiresAt(now.plus(expiration, ChronoUnit.HOURS))
//                .subject(subject)
//                .claim("scope", scope)
//                .claim("payload", payload)
//                .build();
//
//        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
//    }

    public String extractUsername(String token) {
        return extractClaim(token, "sub", String.class);
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
