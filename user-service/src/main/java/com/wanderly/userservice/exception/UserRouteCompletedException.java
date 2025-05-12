package com.wanderly.userservice.exception;

import com.wanderly.common.exception.BadRequestException;

public class UserRouteCompletedException extends BadRequestException {
    public UserRouteCompletedException() {
        super("User route completed!");
    }
}
