package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

/**
 * OCSA 점수로 산출된 주문 난이도 등급 열거형.
 * D1(최저) ~ D5(최고) 5단계로 구분하며,
 * 각 등급에 따라 배정 가능한 기술자의 최소 숙련도 티어가 결정된다.
 */
public enum DifficultyGrade {
    D1, D2, D3, D4, D5;

    /**
     * 이 난이도 등급을 처리하기 위해 필요한 최소 기술자 숙련도 티어 순위를 반환한다.
     * D5는 S(3), D4는 A(2), D3는 B(1), D1/D2는 C(0)
     *
     * @return 필요 티어 순위 (숫자가 클수록 높은 숙련도 요구)
     */
    public int getRequiredTierRank() {
        return switch (this) {
            case D5 -> 3;
            case D4 -> 2;
            case D3 -> 1;
            case D1, D2 -> 0;
        };
    }
}
