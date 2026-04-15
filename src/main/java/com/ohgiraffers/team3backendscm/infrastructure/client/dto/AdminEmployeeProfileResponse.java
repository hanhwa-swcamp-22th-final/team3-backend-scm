package com.ohgiraffers.team3backendscm.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AdminEmployeeProfileResponse {

    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String departmentName;
    private String teamName;
    private String currentTier;
    private BigDecimal totalScore;
    private LocalDate hireDate;
}
