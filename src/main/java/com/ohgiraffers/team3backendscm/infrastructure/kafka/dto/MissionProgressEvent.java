package com.ohgiraffers.team3backendscm.infrastructure.kafka.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MissionProgressEvent {
    private Long employeeId;
    private String missionType;
    private BigDecimal progressValue;
    private Boolean absolute;
}
