package com.wanderly.geoservice.exception;

import com.wanderly.common.exception.NotFoundException;

public class ARModelNotFoundException extends NotFoundException {
    public ARModelNotFoundException() {
        super("AR Model not found");
    }
}
