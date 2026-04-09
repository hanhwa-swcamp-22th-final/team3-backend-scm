package com.ohgiraffers.team3backendscm.scm.command.infrastructure.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OcsaWeightConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOcsaWeightConfigRepository extends JpaRepository<OcsaWeightConfig, Long> {
}
