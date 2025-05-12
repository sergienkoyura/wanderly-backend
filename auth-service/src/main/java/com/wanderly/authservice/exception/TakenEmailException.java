package com.wanderly.authservice.exception;

import com.wanderly.common.exception.BadRequestException;

public class TakenEmailException extends BadRequestException {
    public TakenEmailException() {
        super("Email is already taken");
    }
}
