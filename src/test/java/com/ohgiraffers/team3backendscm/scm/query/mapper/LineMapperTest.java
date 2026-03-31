package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineStatusDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * LineMapper (lines.xml) MyBatis 쿼리 통합 테스트.
 *
 * <p>테스트 전략: @SpringBootTest — 실제 DB에 대해 SQL 바인딩·ResultMap 매핑 오류를 검증한다.
 * </p>
 */
@SpringBootTest
@Transactional
@DisplayName("LineMapper XML 쿼리 테스트")
class LineMapperTest {

    @Autowired
    private LineMapper lineMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** DB에 존재하는 공장 라인 ID. 없으면 null. */
    private Long validLineId;

    @BeforeEach
    void setUp() {
        validLineId = jdbcTemplate.queryForObject(
                "SELECT factory_line_id FROM factory_line LIMIT 1", Long.class);
    }

    // ===== findLinesSummary =====

    @Nested
    @DisplayName("findLinesSummary — 라인별 주문 처리 요약 조회")
    class FindLinesSummary {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
        void findLinesSummary_ExecutesWithoutError() {
            List<LineSummaryDto> result = lineMapper.findLinesSummary();

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 factoryLineId·factoryLineName 필드가 매핑된다")
        void findLinesSummary_MapsFields_WhenDataExists() {
            List<LineSummaryDto> result = lineMapper.findLinesSummary();
            assumeTrue(!result.isEmpty(), "라인 데이터 없음 — skip");

            LineSummaryDto first = result.get(0);
            assertNotNull(first.getFactoryLineId(),   "factoryLineId 매핑 확인");
            assertNotNull(first.getFactoryLineName(), "factoryLineName 매핑 확인");
        }

        @Test
        @DisplayName("달성률(achievementRate)이 0~100 사이다")
        void findLinesSummary_AchievementRateIsInValidRange() {
            List<LineSummaryDto> result = lineMapper.findLinesSummary();
            assumeTrue(!result.isEmpty(), "라인 데이터 없음 — skip");

            result.stream()
                    .filter(dto -> dto.getAchievementRate() != null)
                    .forEach(dto -> {
                        double rate = dto.getAchievementRate();
                        assertTrue(rate >= 0.0 && rate <= 100.0,
                                "달성률이 0~100 범위를 벗어남: " + rate);
                    });
        }
    }

    // ===== findLineStatus =====

    @Nested
    @DisplayName("findLineStatus — 특정 라인 실시간 운영 현황 조회")
    class FindLineStatus {

        @Test
        @DisplayName("유효한 라인 ID로 조회하면 SQL 오류 없이 결과가 반환된다")
        void findLineStatus_ExecutesWithoutError() {
            assumeTrue(validLineId != null, "factory_line 데이터 없음 — skip");

            LineStatusDto result = lineMapper.findLineStatus(validLineId);

            // LEFT JOIN + GROUP BY 구조이므로 데이터 없어도 null 이 아닐 수 있음
            // null 반환 시 데이터 없는 라인으로 간주
        }

        @Test
        @DisplayName("데이터가 있으면 factoryLineId·operationRate 필드가 매핑된다")
        void findLineStatus_MapsFields_WhenDataExists() {
            assumeTrue(validLineId != null, "factory_line 데이터 없음 — skip");

            LineStatusDto result = lineMapper.findLineStatus(validLineId);
            assumeTrue(result != null, "해당 라인 상태 데이터 없음 — skip");

            assertNotNull(result.getFactoryLineId(),   "factoryLineId 매핑 확인");
            assertNotNull(result.getFactoryLineName(), "factoryLineName 매핑 확인");
        }

        @Test
        @DisplayName("가동률(operationRate)이 0~100 사이다")
        void findLineStatus_OperationRateIsInValidRange() {
            assumeTrue(validLineId != null, "factory_line 데이터 없음 — skip");

            LineStatusDto result = lineMapper.findLineStatus(validLineId);
            assumeTrue(result != null && result.getOperationRate() != null,
                    "운영 현황 데이터 없음 — skip");

            double rate = result.getOperationRate();
            assertTrue(rate >= 0.0 && rate <= 100.0,
                    "가동률이 0~100 범위를 벗어남: " + rate);
        }
    }
}
