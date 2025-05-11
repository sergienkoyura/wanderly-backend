package com.wanderly.userservice.repository;

import com.wanderly.userservice.entity.UserARModelCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ARModelCompletionRepository extends JpaRepository<UserARModelCompletion, UUID> {
    Optional<UserARModelCompletion> findByUserIdAndModelId(UUID userId, UUID modelId);

    Optional<UserARModelCompletion> findByModelId(UUID modelId);
}
