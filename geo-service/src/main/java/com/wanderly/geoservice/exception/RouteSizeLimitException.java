package com.wanderly.geoservice.exception;

import com.wanderly.common.exception.BadRequestException;

public class RouteSizeLimitException extends BadRequestException {
    public RouteSizeLimitException() {
        super("You cannot save a route with less than two markers");
    }
}
