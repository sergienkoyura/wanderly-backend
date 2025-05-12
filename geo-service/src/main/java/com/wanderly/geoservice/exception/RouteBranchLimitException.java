package com.wanderly.geoservice.exception;

import com.wanderly.common.exception.BadRequestException;

public class RouteBranchLimitException extends BadRequestException {
    public RouteBranchLimitException() {
        super("You cannot branch from this index! Try decreasing it");
    }
}
