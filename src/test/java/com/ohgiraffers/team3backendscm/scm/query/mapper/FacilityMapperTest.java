package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityHistoryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilitySummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityTrendsDto;
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
 * FacilityMapper (facilities.xml) MyBatis 쿼리 통합 테스트.
 *
 * <p>테스트 전략: @SpringBootTest — 실제 DB에 대해 SQL 바인딩·ResultMap 매핑 오류를 검증한다.
 * 설비(equipment) 데이터가 없으면 파라미터 없는 조회도 빈 결과를 반환하므로,
 * 파라미터가 필요한 조회는 DB에서 유효한 ID를 먼저 조회한 후 테스트한다.
 * </p>
 */
@SpringBootTest
@Transactional
@DisplayName("FacilityMapper XML 쿼리 테스트")
class FacilityMapperTest {

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** DB에 존재하는 설비 ID. 없으면 null. */
    private Long validFacilityId;

    @BeforeEach
    void setUp() {
        validFacilityId = jdbcTemplate.queryForObject(
                "SELECT equipment_id FROM equipment WHERE is_deleted = false LIMIT 1", Long.class);
    }

    // ===== findFacilities =====

    @Nested
    @DisplayName("findFacilities — 전체 설비 목록 조회")
    class FindFacilities {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
        void findFacilities_ExecutesWithoutError() {
            List<FacilityDto> result = facilityMapper.findFacilities();

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 equipmentId·equipmentName 필드가 매핑된다")
        void findFacilities_MapsFields_WhenDataExists() {
            List<FacilityDto> result = facilityMapper.findFacilities();
            assumeTrue(!result.isEmpty(), "설비 데이터 없음 — skip");

            FacilityDto first = result.get(0);
            assertNotNull(first.getEquipmentId(),   "equipmentId 매핑 확인");
            assertNotNull(first.getEquipmentName(), "equipmentName 매핑 확인");
        }
    }

    // ===== findFacilityHistory =====

    @Nested
    @DisplayName("findFacilityHistory — 설비 정비 이력 조회")
    class FindFacilityHistory {

        @Test
        @DisplayName("유효한 설비 ID로 조회하면 SQL 오류 없이 리스트가 반환된다")
        void findFacilityHistory_ExecutesWithoutError() {
            assumeTrue(validFacilityId != null, "설비 데이터 없음 — skip");

            List<FacilityHistoryDto> result = facilityMapper.findFacilityHistory(validFacilityId);

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 eventId·eventType 필드가 매핑된다")
        void findFacilityHistory_MapsFields_WhenDataExists() {
            assumeTrue(validFacilityId != null, "설비 데이터 없음 — skip");

            List<FacilityHistoryDto> result = facilityMapper.findFacilityHistory(validFacilityId);
            assumeTrue(!result.isEmpty(), "해당 설비의 정비 이력 없음 — skip");

            FacilityHistoryDto first = result.get(0);
            assertNotNull(first.getEventId(),   "eventId 매핑 확인");
            assertNotNull(first.getEventType(), "eventType 매핑 확인");
        }

        @Test
        @DisplayName("존재하지 않는 설비 ID로 조회하면 빈 리스트를 반환한다")
        void findFacilityHistory_ReturnsEmpty_WhenFacilityNotExists() {
            List<FacilityHistoryDto> result = facilityMapper.findFacilityHistory(-1L);

            assertNotNull(result);
            assertTrue(result.isEmpty(), "없는 설비 ID 조회는 빈 리스트여야 한다");
        }
    }

    // ===== findFacilityDeployments =====

    @Nested
    @DisplayName("findFacilityDeployments — 설비 배치 인원 조회")
    class FindFacilityDeployments {

        @Test
        @DisplayName("유효한 설비 ID로 조회하면 SQL 오류 없이 리스트가 반환된다")
        void findFacilityDeployments_ExecutesWithoutError() {
            assumeTrue(validFacilityId != null, "설비 데이터 없음 — skip");

            List<FacilityDeploymentDto> result = facilityMapper.findFacilityDeployments(validFacilityId);

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 employeeId·deploymentDate 필드가 매핑된다")
        void findFacilityDeployments_MapsFields_WhenDataExists() {
            assumeTrue(validFacilityId != null, "설비 데이터 없음 — skip");

            List<FacilityDeploymentDto> result = facilityMapper.findFacilityDeployments(validFacilityId);
            assumeTrue(!result.isEmpty(), "해당 설비의 배치 인원 없음 — skip");

            FacilityDeploymentDto first = result.get(0);
            assertNotNull(first.getEmployeeId(),     "employeeId 매핑 확인");
            assertNotNull(first.getDeploymentDate(), "deploymentDate 매핑 확인");
        }
    }

    // ===== findFacilitySummary =====

    @Nested
    @DisplayName("findFacilitySummary — 설비 현황 요약 집계 조회")
    class FindFacilitySummary {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료되고 DTO가 반환된다")
        void findFacilitySummary_ReturnsDto() {
            FacilitySummaryDto result = facilityMapper.findFacilitySummary();

            assertNotNull(result, "요약 DTO가 null 이면 안 된다");
        }

        @Test
        @DisplayName("집계 값들이 음수가 아니다")
        void findFacilitySummary_CountsAreNonNegative() {
            FacilitySummaryDto result = facilityMapper.findFacilitySummary();
            assumeTrue(result.getTotalCount() != null, "집계 데이터 없음 — skip");

            assertTrue(result.getTotalCount()           >= 0, "total_count 음수 불가");
            assertTrue(result.getOperatingCount()       >= 0, "operating_count 음수 불가");
            assertTrue(result.getStoppedCount()         >= 0, "stopped_count 음수 불가");
            assertTrue(result.getUnderInspectionCount() >= 0, "under_inspection_count 음수 불가");
            assertTrue(result.getDisposedCount()        >= 0, "disposed_count 음수 불가");
        }
    }

    // ===== findFacilityTrends =====

    @Nested
    @DisplayName("findFacilityTrends — 설비 환경 트렌드 조회")
    class FindFacilityTrends {

        @Test
        @DisplayName("유효한 설비 ID로 조회하면 SQL 오류 없이 리스트가 반환된다")
        void findFacilityTrends_ExecutesWithoutError() {
            assumeTrue(validFacilityId != null, "설비 데이터 없음 — skip");

            List<FacilityTrendsDto> result = facilityMapper.findFacilityTrends(validFacilityId);

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 equipmentId·detectedAt 필드가 매핑된다")
        void findFacilityTrends_MapsFields_WhenDataExists() {
            assumeTrue(validFacilityId != null, "설비 데이터 없음 — skip");

            List<FacilityTrendsDto> result = facilityMapper.findFacilityTrends(validFacilityId);
            assumeTrue(!result.isEmpty(), "해당 설비의 환경 이벤트 없음 — skip");

            FacilityTrendsDto first = result.get(0);
            assertNotNull(first.getEquipmentId(), "equipmentId 매핑 확인");
            assertNotNull(first.getDetectedAt(),  "detectedAt 매핑 확인");
        }
    }
}
