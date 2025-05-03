package com.wanderly.userservice.dto;

import com.wanderly.common.dto.geo.CityLookupRequest;
import com.wanderly.userservice.enums.ActivityType;
import com.wanderly.userservice.enums.TravelType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserPreferencesDto {
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
    private CityLookupRequest city;
}
