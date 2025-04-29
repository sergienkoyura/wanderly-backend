package com.wanderly.authservice.exception;

import com.wanderly.common.exception.BadRequestException;

public class InvalidTokenException extends BadRequestException {
    public InvalidTokenException() {
        super("Token is invalid");
    }
}
