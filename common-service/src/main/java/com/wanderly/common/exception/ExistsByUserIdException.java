package com.wanderly.common.exception;

public class ExistsByUserIdException extends BadRequestException {
    public ExistsByUserIdException() {
        super("Preferences already exists");
    }
}
