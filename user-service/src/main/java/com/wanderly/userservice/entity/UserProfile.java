package com.wanderly.userservice.entity;

import com.wanderly.userservice.enums.AvatarName;
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
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    private AvatarName avatarName;

    @Column(nullable = false, unique = true)
    private UUID userId; // reference to auth-service user
}
