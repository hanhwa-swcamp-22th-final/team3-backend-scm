package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

/**
 * 기술자 배정 방식을 정의하는 열거형.
 * 주문 난이도(DifficultyGrade)와 기술자 숙련도 티어를 비교하여 자동으로 결정된다.
 *
 * <ul>
 *   <li>GROWTH_TYPE    - 기술자 숙련도가 요구 티어보다 낮은 경우. 성장 기회 제공을 위한 배정.</li>
 *   <li>EFFICIENCY_TYPE - 기술자 숙련도가 요구 티어 이상인 경우. 효율적인 업무 수행을 위한 배정.</li>
 * </ul>
 */
public enum MatchingMode {
    GROWTH_TYPE,
    EFFICIENCY_TYPE;

    /**
     * 주문 난이도와 기술자 숙련도 티어를 비교하여 matching_mode 결정.
     * D5는 S(3), D4는 A(2), D3는 B(1), D1/D2는 C(0)
     * worker_tier < 필요 tier 이면 GROWTH_TYPE
     * worker_tier >= 필요 tier 이면 EFFICIENCY_TYPE
     * difficultyGrade가 null이면 EFFICIENCY_TYPE (기본값)
     *
     * @param difficultyGrade 주문 난이도 등급 (null 허용)
     * @param employeeTier    기술자의 숙련도 티어 문자열 ("S", "A", "B", "C")
     * @return 결정된 배정 방식
     */
    public static MatchingMode determine(DifficultyGrade difficultyGrade, String employeeTier) {
        if (difficultyGrade == null) return EFFICIENCY_TYPE;
        int required = difficultyGrade.getRequiredTierRank();
        int worker   = tierRank(employeeTier);
        return worker < required ? GROWTH_TYPE : EFFICIENCY_TYPE;
    }

    /**
     * 티어 문자열을 숫자 순위로 변환한다.
     * S=3, A=2, B=1, C(기본)=0
     *
     * @param tier 기술자의 숙련도 티어 문자열
     * @return 티어 순위 숫자
     */
    private static int tierRank(String tier) {
        return switch (tier) {
            case "S" -> 3;
            case "A" -> 2;
            case "B" -> 1;
            default  -> 0;
        };
    }
}
