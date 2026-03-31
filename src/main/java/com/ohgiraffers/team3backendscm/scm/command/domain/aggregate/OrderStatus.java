package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

/**
 * 주문(Order)의 처리 상태를 나타내는 열거형.
 * 상태 전이 순서: REGISTERED → ANALYZED → INPROGRESS → COMPLETED
 * ASSIGNED 는 중간 배정 완료 단계로 필요에 따라 사용한다.
 */
public enum OrderStatus {
    REGISTERED,  // 접수 (초기 상태)
    ANALYZED,    // 분석 완료 / 배정 대기
    ASSIGNED,    // 기술자 배정됨
    INPROGRESS,  // 작업 진행 중 (생산 중)
    COMPLETED    // 완료
}
