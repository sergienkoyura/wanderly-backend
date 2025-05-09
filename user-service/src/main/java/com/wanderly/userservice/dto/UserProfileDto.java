package com.wanderly.userservice.dto;

import com.wanderly.userservice.enums.AvatarName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileDto {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    private AvatarName avatarName;
}
