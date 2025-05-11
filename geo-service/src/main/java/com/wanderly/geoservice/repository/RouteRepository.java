package com.wanderly.geoservice.repository;

import com.wanderly.geoservice.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RouteRepository extends JpaRepository<Route, UUID> {
    List<Route> findAllByCityIdAndUserId(UUID cityId, UUID userId);
    Integer countAllByCityIdAndUserId(UUID cityId, UUID userId);

    void deleteByIdAndUserId(UUID routeId, UUID userId);

    Optional<Route> findByIdAndUserId(UUID routeId, UUID userId);
}
