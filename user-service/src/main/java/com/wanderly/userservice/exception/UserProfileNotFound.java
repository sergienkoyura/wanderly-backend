package com.wanderly.userservice.exception;

import com.wanderly.common.exception.NotFoundException;

public class UserProfileNotFound extends NotFoundException {
    public UserProfileNotFound() {
        super("User profile not found");
    }
}
