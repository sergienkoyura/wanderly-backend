package com.wanderly.common.dto.auth;

public record VerificationEmailMessage(String email, String verificationCode) {
}
