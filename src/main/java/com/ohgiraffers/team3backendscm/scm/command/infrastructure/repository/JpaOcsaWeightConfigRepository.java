package com.ohgiraffers.team3backendscm.scm.command.infrastructure.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OcsaWeightConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaOcsaWeightConfigRepository extends JpaRepository<OcsaWeightConfig, Long> {

    @Override
    @Query(value = "SELECT * FROM OCSA_weight_config WHERE config_id = :configId", nativeQuery = true)
    Optional<OcsaWeightConfig> findById(@Param("configId") Long configId);
}
