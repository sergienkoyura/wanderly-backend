package com.wanderly.geoservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteMarker {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Integer orderIndex;
    private Integer stayingTime;

    @ManyToOne
    @JoinColumn(name = "marker_id")
    private Marker marker;

    @ManyToOne
    @JoinColumn(name = "route_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Route route;
}
