package com.wanderly.geoservice.repository;

import com.wanderly.geoservice.entity.Marker;
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
    List<Marker> findAllUnusedByCityIdAndUserId(@Param("cityId") UUID cityId,@Param("userId") UUID userId);

    @Query("""
                select m from Marker m
                left join RouteMarker rm on rm.marker.id = m.id
                where rm.id is null and m.cityId = :cityId
            """)
    List<Marker> findAllUnusedByCityId(@Param("cityId") UUID cityId);
}

//select count(*)
//from marker m
//         join route_marker on m.id = route_marker.marker_id
//         join public.route r on route_marker.route_id = r.id
//where m.city_id = '7681df13-0c62-4333-866e-fe73bb93a460'
//  and user_id != '7681df13-0c62-4333-866e-fe73bb93a460';
//
//select count(*)
//from marker
//         left join route_marker on marker.id = route_marker.marker_id
//where route_marker.id is null and marker.city_id = '7681df13-0c62-4333-866e-fe73bb93a460';