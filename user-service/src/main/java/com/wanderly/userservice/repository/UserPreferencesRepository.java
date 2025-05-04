package com.wanderly.userservice.repository;

import com.wanderly.userservice.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {
    Boolean existsByUserId(UUID userId);

    Optional<UserPreferences> findByUserId(UUID userId);
}
