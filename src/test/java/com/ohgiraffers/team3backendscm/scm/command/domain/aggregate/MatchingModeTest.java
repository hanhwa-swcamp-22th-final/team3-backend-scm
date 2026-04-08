package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * MatchingMode 자동 결정 로직을 검증하는 단위 테스트.
 *
 * <p>테스트 전략: 순수 단위 테스트 + 매개변수화 테스트 (@ParameterizedTest)
 * - 주문 난이도(DifficultyGrade: D1~D5)와 기술자 역량 등급(C/B/A/S) 조합에 따라
 *   MatchingMode가 올바르게 결정되는지 전수 검증한다.
 * - 역량이 요구 수준 이상이면 EFFICIENCY_TYPE(효율 우선),
 *   미달이면 GROWTH_TYPE(성장 우선)을 반환해야 한다.
 * - 난이도가 null인 경우 항상 EFFICIENCY_TYPE을 반환하는 예외 처리도 검증한다.
 * </p>
 */
@DisplayName("MatchingMode 자동 결정 로직")
class MatchingModeTest {

    // 난이도·역량 조합별 기대 MatchingMode를 CSV 형태로 정의
    @ParameterizedTest(name = "난이도={0}, 역량={1} → {2}")
    @CsvSource({
            // D5 (Critical) → S(3) 필요
            "D5, S, EFFICIENCY_TYPE",   // S(3) >= S(3)
            "D5, A, GROWTH_TYPE",       // A(2) < S(3)
            "D5, B, GROWTH_TYPE",       // B(1) < S(3)
            "D5, C, GROWTH_TYPE",       // C(0) < S(3)
            // D4 (High) → A(2) 필요
            "D4, S, EFFICIENCY_TYPE",   // S(3) >= A(2)
            "D4, A, EFFICIENCY_TYPE",   // A(2) >= A(2)
            "D4, B, GROWTH_TYPE",       // B(1) < A(2)
            "D4, C, GROWTH_TYPE",       // C(0) < A(2)
            // D3 (Normal) → B(1) 필요
            "D3, S, EFFICIENCY_TYPE",   // S(3) >= B(1)
            "D3, A, EFFICIENCY_TYPE",   // A(2) >= B(1)
            "D3, B, EFFICIENCY_TYPE",   // B(1) >= B(1)
            "D3, C, GROWTH_TYPE",       // C(0) < B(1)
            // D2 (Easy) → C(0) 필요, 모든 등급 가능
            "D2, S, EFFICIENCY_TYPE",
            "D2, A, EFFICIENCY_TYPE",
            "D2, B, EFFICIENCY_TYPE",
            "D2, C, EFFICIENCY_TYPE",
            // D1 (Simple) → C(0) 필요, 모든 등급 가능
            "D1, S, EFFICIENCY_TYPE",
            "D1, A, EFFICIENCY_TYPE",
            "D1, B, EFFICIENCY_TYPE",
            "D1, C, EFFICIENCY_TYPE",
    })
    @DisplayName("난이도와 역량 조합에 따라 올바른 MatchingMode를 반환한다")
    void determine_ReturnsCorrectMode(String grade, String tier, String expected) {
        // given - 문자열로 전달된 난이도·역량 파라미터를 도메인 타입으로 변환
        DifficultyGrade difficultyGrade = DifficultyGrade.valueOf(grade);

        // when - MatchingMode 자동 결정 로직 실행
        MatchingMode result = MatchingMode.determine(difficultyGrade, tier);

        // then - 기대 MatchingMode와 일치하는지 검증
        assertEquals(MatchingMode.valueOf(expected), result);
    }

    // 난이도가 null인 경계 케이스: 역량 등급 무관하게 항상 EFFICIENCY_TYPE 반환 검증
    @ParameterizedTest(name = "난이도=null, 역량={0} → EFFICIENCY_TYPE")
    @CsvSource({"S", "A", "B", "C"})
    @DisplayName("난이도가 null이면 항상 EFFICIENCY_TYPE을 반환한다")
    void determine_NullDifficulty_AlwaysEfficiency(String tier) {
        // when - 난이도 null로 MatchingMode 결정 시도
        MatchingMode result = MatchingMode.determine(null, tier);

        // then - 역량에 관계없이 EFFICIENCY_TYPE을 반환해야 한다
        assertEquals(MatchingMode.EFFICIENCY_TYPE, result);
    }
}
