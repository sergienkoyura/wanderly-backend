package com.wanderly.userservice.dto;

import com.wanderly.common.dto.geo.CityDto;
import com.wanderly.userservice.enums.ActivityType;
import com.wanderly.userservice.enums.TravelType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class UserPreferencesDto {
    private UUID id;

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    private TravelType travelType;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer timePerRoute;

    @NotNull
    private ActivityType activityType;

    @Valid
    @NotNull
    private CityDto city;
}
