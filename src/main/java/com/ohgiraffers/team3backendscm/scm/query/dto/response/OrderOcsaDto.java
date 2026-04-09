package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.DifficultyGrade;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderOcsaDto {

    private Long orderId;
    private BigDecimal v1ProcessComplexity;
    private BigDecimal v2QualityPrecision;
    private BigDecimal v3CapacityRequirements;
    private BigDecimal v4SpaceTimeUrgency;
    private BigDecimal alphaNovlety;
    private BigDecimal difficultyScore;
    private DifficultyGrade difficultyGrade;
    private Long recommendedTechnicianId;
}
