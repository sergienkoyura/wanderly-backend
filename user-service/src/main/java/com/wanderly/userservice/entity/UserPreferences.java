package com.wanderly.userservice.entity;

import com.wanderly.userservice.enums.ActivityType;
import com.wanderly.userservice.enums.TravelType;
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
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(nullable = false, unique = true)
    private UUID userId; // reference to auth-service user

    @Enumerated(EnumType.STRING)
    private TravelType travelType;

    private Integer timePerRoute;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

//    private Boolean notifications;
//    private Boolean geoposition;
//    private Boolean healthKit;

    private UUID cityId; // reference to geo-service city

}
