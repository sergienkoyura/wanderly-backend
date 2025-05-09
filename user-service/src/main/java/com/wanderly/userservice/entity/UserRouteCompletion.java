package com.wanderly.userservice.entity;

import com.wanderly.userservice.enums.RouteStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRouteCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RouteStatus status;
    private Integer step;

    private UUID userId; // reference to auth-service user

    @Column(nullable = false, unique = true)
    private UUID routeId; // reference to geo-service user
}
