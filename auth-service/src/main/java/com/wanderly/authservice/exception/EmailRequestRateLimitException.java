package com.wanderly.authservice.exception;

import com.wanderly.common.exception.RateLimitException;

public class EmailRequestRateLimitException extends RateLimitException {
    public EmailRequestRateLimitException() {
        super("Please wait before requesting another verification email");
    }
}
