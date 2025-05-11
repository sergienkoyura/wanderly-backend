package com.wanderly.geoservice.entity;

import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.MarkerTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ar_model")
@Builder
public class ARModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Double latitude;
    private Double longitude;
    private Integer code;
    private UUID cityId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
