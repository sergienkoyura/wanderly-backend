package com.wanderly.userservice.repository;

import com.wanderly.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Boolean existsByUserId(UUID userId);

    Optional<UserProfile> findByUserId(UUID userId);
}
