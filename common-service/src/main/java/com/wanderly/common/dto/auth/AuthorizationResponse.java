package com.wanderly.common.dto.auth;

public record AuthorizationResponse(String accessToken, String refreshToken) {
}
