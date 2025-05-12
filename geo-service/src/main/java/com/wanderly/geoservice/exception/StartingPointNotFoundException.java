package com.wanderly.geoservice.exception;

import com.wanderly.common.exception.NotFoundException;

public class StartingPointNotFoundException extends NotFoundException {
    public StartingPointNotFoundException() {
        super("Starting point not found");
    }
}
