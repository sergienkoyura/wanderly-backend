package com.wanderly.geoservice.repository;

import com.wanderly.geoservice.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RouteRepository extends JpaRepository<Route, UUID> {
    List<Route> findAllByCityId(UUID cityId);
}
