package com.wanderly.geoservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer placeId;
    private String name;
    private String details;
    private Double latitude;
    private Double longitude;

    @ElementCollection
    private List<Double> boundingBox;
}
