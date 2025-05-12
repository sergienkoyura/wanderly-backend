package com.wanderly.geoservice.exception;

import com.wanderly.common.exception.NotFoundException;

public class UserPreferencesNotFound extends NotFoundException {
    public UserPreferencesNotFound() {
        super("User preferences not found");
    }
}
