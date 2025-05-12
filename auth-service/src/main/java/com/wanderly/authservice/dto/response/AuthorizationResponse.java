package com.wanderly.authservice.dto.response;

public record AuthorizationResponse(String accessToken, String refreshToken) {
}
