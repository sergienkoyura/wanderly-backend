package com.wanderly.authservice.exception;

import com.wanderly.common.exception.NotFoundException;

public class AccountNotFoundException extends NotFoundException {
    public AccountNotFoundException() {
        super("Account with this email does not exist");
    }
}
