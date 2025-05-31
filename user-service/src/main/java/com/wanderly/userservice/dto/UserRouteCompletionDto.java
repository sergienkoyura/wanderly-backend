package com.wanderly.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wanderly.userservice.enums.RouteStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRouteCompletionDto {
    @NotNull
    private RouteStatus status;

    @NotNull
    @Min(0)
    private Integer step;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String cityName;

    @NotNull
    private UUID routeId;
}
