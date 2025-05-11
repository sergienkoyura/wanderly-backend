package com.wanderly.geoservice.repository;

import com.wanderly.geoservice.entity.ARModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ARModelRepository extends JpaRepository<ARModel, UUID> {
    List<ARModel> findAllByCityId(UUID cityId);
}
