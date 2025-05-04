package com.wanderly.geoservice.repository;

import com.wanderly.geoservice.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID> {
    Optional<City> findByOsmId(Integer osmId);
}
