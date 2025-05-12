package com.wanderly.userservice.repository;

import com.wanderly.userservice.dto.CityStatisticsDto;
import com.wanderly.userservice.entity.UserRouteCompletion;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRouteCompletionRepository extends JpaRepository<UserRouteCompletion, UUID> {
    Optional<UserRouteCompletion> findByRouteIdAndUserId(UUID routeId, UUID userId);

    void deleteByRouteId(UUID routeId);


    @Query("""
                SELECT
                COUNT(DISTINCT urc.id)
                FROM UserRouteCompletion urc
                WHERE urc.userId = :userId AND urc.status = 'DONE'
            """)
    int countCompletedRoutesByUserId(@Param("userId") UUID userId);

    @Query("""
                select
                urc.cityName as cityName,
                sum(CASE WHEN urc.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as inProgressRoutes,
                sum(CASE WHEN urc.status = 'DONE' THEN 1 ELSE 0 END) as completedRoutes
                FROM UserRouteCompletion urc
                WHERE urc.userId = :userId
                GROUP BY urc.cityName
            """)
    List<Tuple> getRouteStatsPerCity(@Param("userId") UUID userId);
}
