package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

public enum MatchingStatus {
    RECOMMEND,  // 추천
    APPLY,      // 지원
    APPROVE,    // 승인
    CONFIRM,    // 배정 확정
    REJECT,     // 거절
    COMPLETE    // 완료
}
