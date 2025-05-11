package com.wanderly.geoservice.exception;

import com.wanderly.common.exception.RateLimitException;

public class RouteRateLimitException extends RateLimitException {
    public RouteRateLimitException() {
        super("You exceeded the maximum number of routes");
    }
}
