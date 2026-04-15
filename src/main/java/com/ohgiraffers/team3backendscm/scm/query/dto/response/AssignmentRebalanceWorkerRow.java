package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentRebalanceWorkerRow {

    private Long factoryLineId;
    private String factoryLineName;
    private Long employeeId;
}
