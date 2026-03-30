package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 특정 공장 라인의 실시간 운영 현황을 담는 응답 DTO.
 * 배치된 기술자 수, 진행 중인 주문 수, 설비 가동률 등을 포함하여
 * 팀 리더가 개별 라인 상태를 상세히 파악할 수 있도록 한다.
 * GET /api/v1/scm/lines/{lineId}/status 에서 반환된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineStatusDto {

    private Long factoryLineId;               // 공장 라인 PK
    private String factoryLineName;           // 공장 라인 명칭
    private Integer assignedTechnicianCount;  // 현재 배정된 기술자 수
    private Integer inProgressOrderCount;     // 현재 진행 중인 주문 수 (INPROGRESS 상태)
    private Integer totalEquipmentCount;      // 라인에 속한 전체 설비 수
    private Integer operatingEquipmentCount;  // 현재 정상 가동 중인 설비 수
    private Double operationRate;             // 설비 가동률 (operatingEquipmentCount / totalEquipmentCount)
}
