package com.wanderly.geoservice.exception;

import com.wanderly.common.exception.NotFoundException;

public class RouteNotFoundException extends NotFoundException {
    public RouteNotFoundException() {
        super("Route not found or you don't have permission to access it");
    }
}
