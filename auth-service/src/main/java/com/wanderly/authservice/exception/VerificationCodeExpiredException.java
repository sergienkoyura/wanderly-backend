package com.wanderly.authservice.exception;

import com.wanderly.common.exception.BadRequestException;

public class VerificationCodeExpiredException extends BadRequestException {
    public VerificationCodeExpiredException() {
        super("Verification code expired");
    }
}
