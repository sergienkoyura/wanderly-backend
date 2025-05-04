package com.wanderly.userservice.exception;

import com.wanderly.common.exception.BadRequestException;

public class ExistsByUserIdException extends BadRequestException {
    public ExistsByUserIdException() {
        super("Preferences already exists");
    }
}
