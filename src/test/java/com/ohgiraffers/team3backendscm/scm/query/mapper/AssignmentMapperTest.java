package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentCandidateDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentRebalanceDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentTimelineDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * AssignmentMapper (assignments.xml) MyBatis 쿼리 통합 테스트.
 *
 * <p>테스트 전략: @SpringBootTest — 실제 DB에 대해 SQL 바인딩·ResultMap 매핑 오류를 검증한다.
 * @Transactional 로 각 테스트 후 롤백하여 DB 상태를 유지한다.
 * DB에 조회할 데이터가 없는 경우 assumeTrue 로 해당 테스트를 건너뛴다.
 * </p>
 */
@SpringBootTest
@Transactional
@DisplayName("AssignmentMapper XML 쿼리 테스트")
class AssignmentMapperTest {

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ===== findById =====

    @Nested
    @DisplayName("findById — 배정 기록 단건 조회")
    class FindById {

        @Test
        @DisplayName("존재하는 ID로 조회하면 Optional에 값이 담긴다")
        void findById_ReturnsValue_WhenExists() {
            Long id = jdbcTemplate.queryForObject(
                    "SELECT matching_record_id FROM matching_record LIMIT 1", Long.class);
            assumeTrue(id != null, "matching_record 데이터 없음 — skip");

            Optional<AssignmentDetailDto> result = assignmentMapper.findById(id);

            assertTrue(result.isPresent(), "존재하는 ID 조회 결과가 비어있으면 안 된다");
            assertNotNull(result.get().getMatchingRecordId(), "matchingRecordId 매핑 확인");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 Optional.empty()를 반환한다")
        void findById_ReturnsEmpty_WhenNotExists() {
            Optional<AssignmentDetailDto> result = assignmentMapper.findById(-1L);

            assertTrue(result.isEmpty(), "없는 ID 조회 결과는 empty 여야 한다");
        }
    }

    // ===== findCandidates =====

    @Nested
    @DisplayName("findCandidates — 배정 후보 기술자 목록 조회")
    class FindCandidates {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
        void findCandidates_ExecutesWithoutError() {
            List<AssignmentCandidateDto> result = assignmentMapper.findCandidates();

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 employeeId·tier 필드가 매핑된다")
        void findCandidates_MapsFields_WhenDataExists() {
            List<AssignmentCandidateDto> result = assignmentMapper.findCandidates();
            assumeTrue(!result.isEmpty(), "WORKER 직원 데이터 없음 — skip");

            AssignmentCandidateDto first = result.get(0);
            assertNotNull(first.getEmployeeId(), "employeeId 매핑 확인");
            assertNotNull(first.getTier(),       "tier 매핑 확인");
        }
    }

    // ===== findSummary =====

    @Nested
    @DisplayName("findSummary — 배정 현황 요약 집계 조회")
    class FindSummary {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료되고 DTO가 반환된다")
        void findSummary_ReturnsDto() {
            AssignmentSummaryDto result = assignmentMapper.findSummary();

            assertNotNull(result, "요약 DTO가 null 이면 안 된다");
        }
    }

    // ===== findTimeline =====

    @Nested
    @DisplayName("findTimeline — 라인별 배정 타임라인 조회")
    class FindTimeline {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
        void findTimeline_ExecutesWithoutError() {
            List<AssignmentTimelineDto> result = assignmentMapper.findTimeline();

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 factoryLineId·matchingStatus 필드가 매핑된다")
        void findTimeline_MapsFields_WhenDataExists() {
            List<AssignmentTimelineDto> result = assignmentMapper.findTimeline();
            assumeTrue(!result.isEmpty(), "타임라인 데이터 없음 — skip");

            AssignmentTimelineDto first = result.get(0);
            assertNotNull(first.getFactoryLineId(),  "factoryLineId 매핑 확인");
            assertNotNull(first.getMatchingStatus(), "matchingStatus 매핑 확인");
        }
    }

    // ===== findRebalance =====

    @Nested
    @DisplayName("findRebalance — 라인별 기술자 재조정 현황 조회")
    class FindRebalance {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
        void findRebalance_ExecutesWithoutError() {
            List<AssignmentRebalanceDto> result = assignmentMapper.findRebalance();

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 factoryLineId·totalTechnicianCount 필드가 매핑된다")
        void findRebalance_MapsFields_WhenDataExists() {
            List<AssignmentRebalanceDto> result = assignmentMapper.findRebalance();
            assumeTrue(!result.isEmpty(), "라인 데이터 없음 — skip");

            AssignmentRebalanceDto first = result.get(0);
            assertNotNull(first.getFactoryLineId(),        "factoryLineId 매핑 확인");
            assertNotNull(first.getTotalTechnicianCount(), "totalTechnicianCount 매핑 확인");
        }
    }
}
