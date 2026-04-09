package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 氚办爼 旮办垹??氤�瓴??鞍?? ?旍箔 DTO.
 * PUT /api/v1/scm/assignments/{matchingRecordId} ?愳劀 ?毄?滊嫟.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReassignRequest {

    /** ?堧 氚办爼??旮办垹??employee_id) */
    private Long technicianId;
}
