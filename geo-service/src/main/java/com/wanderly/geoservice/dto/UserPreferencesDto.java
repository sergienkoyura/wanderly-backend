package com.wanderly.geoservice.dto;

import com.wanderly.geoservice.enums.ActivityType;
import com.wanderly.geoservice.enums.TravelType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPreferencesDto {
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
