package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 諛곗젙 湲곗닠??蹂寃??щ같?? ?붿껌 DTO.
 * PUT /api/v1/scm/assignments/{matchingRecordId} ?먯꽌 ?ъ슜?쒕떎.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReassignRequest {

    /** ?덈줈 諛곗젙??湲곗닠??employee_id) */
    private Long technicianId;
}
