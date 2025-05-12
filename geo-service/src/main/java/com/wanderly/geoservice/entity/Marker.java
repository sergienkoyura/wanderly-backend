package com.wanderly.geoservice.entity;

import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.MarkerTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Marker {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Double latitude;
    private Double longitude;
    private String name;
    @Enumerated(EnumType.STRING)
    private MarkerTag tag;
    @Enumerated(EnumType.STRING)
    private MarkerCategory category;
    private UUID cityId;
    private Double rating;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
