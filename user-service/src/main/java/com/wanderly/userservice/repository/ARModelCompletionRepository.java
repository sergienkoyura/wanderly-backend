package com.wanderly.userservice.repository;

import com.wanderly.userservice.entity.UserARModelCompletion;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ARModelCompletionRepository extends JpaRepository<UserARModelCompletion, UUID> {
    Optional<UserARModelCompletion> findByUserIdAndModelId(UUID userId, UUID modelId);

    Optional<UserARModelCompletion> findByModelId(UUID modelId);

    @Query("""
                SELECT
                COUNT(DISTINCT umc.id)
                FROM UserARModelCompletion umc
                WHERE umc.userId = :userId
            """)
    int countCompletedARModelsByUserId(@Param("userId") UUID userId);

    @Query("""
                select
                umc.cityName as cityName,
                COUNT(DISTINCT umc.id) as completedARModels
                FROM UserARModelCompletion umc
                WHERE umc.userId = :userId
                GROUP BY umc.cityName
            """)
    List<Tuple> getARModelStatsPerCity(@Param("userId") UUID userId);
}
