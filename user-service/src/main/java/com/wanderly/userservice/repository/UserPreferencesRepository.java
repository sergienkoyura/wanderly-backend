package com.wanderly.userservice.repository;

import com.wanderly.userservice.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    Boolean existsByUserId(UUID userId);
}
