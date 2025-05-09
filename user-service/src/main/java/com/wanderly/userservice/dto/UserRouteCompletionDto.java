package com.wanderly.userservice.dto;

import com.wanderly.userservice.enums.RouteStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
public class UserRouteCompletionDto {
    @NotNull
    private RouteStatus status;

    @NotNull
    @Min(2)
    private Integer step;

    @NotNull
    private UUID routeId;
}
