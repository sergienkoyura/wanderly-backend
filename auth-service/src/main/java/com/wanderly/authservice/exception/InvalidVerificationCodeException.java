package com.wanderly.authservice.exception;

import com.wanderly.common.exception.BadRequestException;

public class InvalidVerificationCodeException extends BadRequestException {
    public InvalidVerificationCodeException() {
        super("Invalid verification code");
    }
}
