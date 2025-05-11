package com.wanderly.userservice.exception;

import com.wanderly.common.exception.NotFoundException;

public class UserRouteCompletionNotFound extends NotFoundException {
    public UserRouteCompletionNotFound() {
        super("User route completion not found");
    }
}
