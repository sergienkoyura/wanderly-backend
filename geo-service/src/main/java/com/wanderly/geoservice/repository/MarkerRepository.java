package com.wanderly.geoservice.repository;

import com.wanderly.geoservice.entity.Marker;
import com.wanderly.geoservice.enums.MarkerCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MarkerRepository extends JpaRepository<Marker, UUID> {
    boolean existsByCityId(UUID cityId);

    List<Marker> findAllByCityId(UUID cityId);

    @Query("""
                select m from Marker m
                join RouteMarker rm on rm.marker.id = m.id
                join Route r on r.id = rm.route.id
                where m.cityId = :cityId and r.userId != :userId
            """)
    List<Marker> findAllUnusedByCityIdAndUserId(@Param("cityId") UUID cityId,
                                                @Param("userId") UUID userId);

    @Query("""
                select m from Marker m
                left join RouteMarker rm on rm.marker.id = m.id
                where rm.id is null and m.cityId = :cityId
            """)
    List<Marker> findAllUnusedByCityId(@Param("cityId") UUID cityId);


    Integer countAllByCityId(UUID cityId);

    @Query("""
                select m from Marker m
                join RouteMarker rm on rm.marker.id = m.id
                join Route r on r.id = rm.route.id
                where m.cityId = :cityId and r.userId != :userId or
                        m.cityId = :cityId and r.id = :routeId
            """)
    List<Marker> findAllUnusedByCityIdAndUserIdExceptRouteId(@Param("cityId") UUID cityId,
                                                             @Param("userId") UUID userId,
                                                             @Param("routeId") UUID routeId);

    List<Marker> findAllByCityIdAndCategory(UUID cityId, MarkerCategory nature);
}
