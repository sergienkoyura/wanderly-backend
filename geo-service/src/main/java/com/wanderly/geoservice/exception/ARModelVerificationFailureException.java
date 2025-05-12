package com.wanderly.geoservice.exception;

import com.wanderly.common.exception.BadRequestException;
import com.wanderly.common.exception.NotFoundException;

public class ARModelVerificationFailureException extends BadRequestException {
    public ARModelVerificationFailureException() {
        super("Code is invalid");
    }
}
