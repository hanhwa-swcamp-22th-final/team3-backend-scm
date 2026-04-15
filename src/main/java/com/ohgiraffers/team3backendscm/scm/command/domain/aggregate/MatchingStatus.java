package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

/**
 * 기술자 배정(MatchingRecord)의 처리 상태를 정의하는 열거형.
 * 시스템 추천 후 기술자 지원 또는 팀 리더 승인 거쳐 확정 순으로 전이되며,
 * 거절(REJECT) 또는 완료(COMPLETE) 로 종료될 수 있다.
 */
public enum MatchingStatus {
    RECOMMEND,  // 추천 (시스템이 후보 기술자를 추천한 상태)
    APPLY,      // 지원 (기술자가 직접 배정에 지원한 상태)
    APPROVE,    // 승인 (팀 리더가 배정된 건을 승인한 상태)
    CONFIRM,    // 배정 확정 (최종 배정이 확정된 상태)
    INPROGRESS, // 작업 진행 중
    REJECT,     // 거절 (배정을 거절한 상태)
    COMPLETE    // 완료 (작업이 완료된 상태)
}
