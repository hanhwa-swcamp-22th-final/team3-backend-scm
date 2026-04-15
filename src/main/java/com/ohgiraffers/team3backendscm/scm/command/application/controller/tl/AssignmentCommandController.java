package com.ohgiraffers.team3backendscm.scm.command.application.controller.tl;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.AssignRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ReassignRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.tl.AssignmentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 팀 리더(TL)의 기술자 배정 Command REST 컨트롤러.
 * 기본 경로: /api/v1/scm
 * <p>
 * 해당 기능: 기술자를 주문에 배정 (POST /api/v1/scm/assignments)
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('TL')")
public class AssignmentCommandController {

    private final AssignmentCommandService assignmentCommandService;

    /**
     * 기술자를 주문에 배정한다.
     * 요청 바디의 orderId 와 technicianId 를 기반으로 배정 처리를 진행하고,
     * 성공 시 데이터 없는 성공 응답을 반환한다.
     *
     * @param request 배정 요청 DTO (orderId, technicianId)
     * @return 성공 여부만 담은 ApiResponse (data = null)
     */
    @PostMapping("/assignments")
    public ResponseEntity<ApiResponse<Void>> createAssignment(@RequestBody AssignRequest request) {
        assignmentCommandService.assign(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 배정된 기술자를 변경(재배치)한다.
     * 새 기술자의 숙련도 티어를 조회하여 MatchingMode 를 재계산하고 기록을 갱신한다.
     *
     * @param matchingRecordId 변경할 배정 기록 ID
     * @param request          새 기술자 ID를 담은 요청 DTO
     * @return 성공 여부만 담은 ApiResponse (data = null)
     */
    @PutMapping("/assignments/{matchingRecordId}")
    public ResponseEntity<ApiResponse<Void>> reassignAssignment(
            @PathVariable Long matchingRecordId,
            @RequestBody ReassignRequest request) {
        assignmentCommandService.reassign(matchingRecordId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 배정을 취소한다.
     * MatchingRecord 상태를 REJECT 로 변경하고 주문 상태를 ANALYZED 로 롤백한다.
     *
     * @param matchingRecordId 취소할 배정 기록 ID
     * @return 성공 여부만 담은 ApiResponse (data = null)
     */
    @DeleteMapping("/assignments/{matchingRecordId}")
    public ResponseEntity<ApiResponse<Void>> cancelAssignment(@PathVariable Long matchingRecordId) {
        assignmentCommandService.cancel(matchingRecordId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
