package com.wanderly.geoservice.exception;

import com.wanderly.common.exception.BadRequestException;

public class ARModelVerificationFailureException extends BadRequestException {
    public ARModelVerificationFailureException() {
        super("Code is invalid");
    }
}
