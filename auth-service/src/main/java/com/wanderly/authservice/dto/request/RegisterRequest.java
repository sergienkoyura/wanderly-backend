package com.wanderly.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(
                max = 64,
                message = "Password is too long"
        )
        @Pattern(
                regexp = "^(?=.*\\d).{8,64}$",
                message = "Password should be at least 8 characters including a number"
        )
        String password,

        @Size(max = 6)
        String code) {
}
