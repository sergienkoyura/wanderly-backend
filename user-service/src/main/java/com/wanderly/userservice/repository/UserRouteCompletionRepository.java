package com.wanderly.userservice.repository;

import com.wanderly.userservice.entity.UserRouteCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRouteCompletionRepository extends JpaRepository<UserRouteCompletion, UUID> {
    Optional<UserRouteCompletion> findByRouteIdAndUserId(UUID routeId, UUID userId);

    void deleteByRouteId(UUID routeId);
}
