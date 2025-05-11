package com.wanderly.geoservice.exception;

import com.wanderly.common.exception.NotFoundException;

public class MarkerNotFoundException extends NotFoundException {
    public MarkerNotFoundException() {
        super("Marker not found");
    }
}
