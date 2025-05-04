package com.wanderly.common.util;

import com.nimbusds.jwt.SignedJWT;
import com.wanderly.common.exception.BadRequestException;

import java.util.UUID;

public class JwtUtil {

    public static UUID extractUserId(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            SignedJWT signedJWT = SignedJWT.parse(token);
            String subject = signedJWT.getJWTClaimsSet().getSubject();
            return UUID.fromString(subject);
        } catch (Exception e) {
            throw new BadRequestException("Invalid JWT");
        }
    }
}