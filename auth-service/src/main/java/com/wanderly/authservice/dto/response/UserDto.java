package com.wanderly.authservice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {
    private String email;
    private LocalDateTime createdAt;
}