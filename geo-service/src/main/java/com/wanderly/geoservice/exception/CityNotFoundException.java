package com.wanderly.geoservice.exception;

import com.wanderly.common.exception.NotFoundException;

public class CityNotFoundException extends NotFoundException {
    public CityNotFoundException() {
        super("City not found");
    }
}
