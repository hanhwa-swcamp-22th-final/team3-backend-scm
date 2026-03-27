package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

public enum OrderStatus {
    REGISTERED,  // 접수 (초기 상태)
    ANALYZED,    // 분석 완료 / 배정 대기
    ASSIGNED,    // 기술자 배정됨
    INPROGRESS,  // 작업 진행 중 (생산 중)
    COMPLETED    // 완료
}
