package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.TaskDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerMatchingHistoryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerTaskSummaryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * WorkerMapper (workers.xml) MyBatis 쿼리 통합 테스트.
 *
 * <p>테스트 전략: @SpringBootTest — 실제 DB에 대해 SQL 바인딩·ResultMap 매핑 오류를 검증한다.
 * 테스트 ID는 Admin DB 의 employee 테이블에 의존하지 않는 값으로 고정한다.
 * </p>
 */
@SpringBootTest
@Transactional
@DisplayName("WorkerMapper XML 쿼리 테스트")
class WorkerMapperTest {

    @Autowired
    private WorkerMapper workerMapper;

    /** SCM 쿼리의 employee_id 필터 검증용 ID. Admin DB 존재 여부와 무관하다. */
    private final Long workerEmployeeId = 999_999_999_999L;

    // ===== findMyPendingTasks =====

    @Nested
    @DisplayName("findMyPendingTasks — 미완료 작업 목록 조회")
    class FindMyPendingTasks {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
        void findMyPendingTasks_ExecutesWithoutError() {
            List<TaskDto> result = workerMapper.findMyPendingTasks(workerEmployeeId);

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("REJECT·COMPLETE 상태 작업은 결과에 포함되지 않는다")
        void findMyPendingTasks_ExcludesRejectedAndCompleted() {
            List<TaskDto> result = workerMapper.findMyPendingTasks(workerEmployeeId);

            result.forEach(task -> {
                String status = task.getMatchingStatus();
                assertTrue(
                        !"REJECT".equals(status) && !"COMPLETE".equals(status),
                        "REJECT·COMPLETE 상태가 결과에 포함되어서는 안 된다: " + status
                );
            });
        }

        @Test
        @DisplayName("존재하지 않는 직원 ID로 조회하면 빈 리스트를 반환한다")
        void findMyPendingTasks_ReturnsEmpty_WhenEmployeeNotExists() {
            List<TaskDto> result = workerMapper.findMyPendingTasks(-1L);

            assertNotNull(result);
            assertTrue(result.isEmpty(), "존재하지 않는 직원 ID 조회는 빈 리스트여야 한다");
        }
    }

    // ===== findMyDeployments =====

    @Nested
    @DisplayName("findMyDeployments — 설비 배치 이력 조회")
    class FindMyDeployments {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
        void findMyDeployments_ExecutesWithoutError() {
            List<WorkerDeploymentDto> result = workerMapper.findMyDeployments(workerEmployeeId);

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 facilityId·deploymentDate 필드가 매핑된다")
        void findMyDeployments_MapsFields_WhenDataExists() {
            List<WorkerDeploymentDto> result = workerMapper.findMyDeployments(workerEmployeeId);
            assumeTrue(!result.isEmpty(), "배치 이력 데이터 없음 — skip");

            WorkerDeploymentDto first = result.get(0);
            assertNotNull(first.getFacilityId(),     "facilityId 매핑 확인");
            assertNotNull(first.getDeploymentDate(), "deploymentDate 매핑 확인");
        }
    }

    // ===== findMyMatchingHistory =====

    @Nested
    @DisplayName("findMyMatchingHistory — 주문 배정 이력 조회")
    class FindMyMatchingHistory {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
        void findMyMatchingHistory_ExecutesWithoutError() {
            List<WorkerMatchingHistoryDto> result = workerMapper.findMyMatchingHistory(workerEmployeeId);

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 matchingRecordId·orderNumber·status 필드가 매핑된다")
        void findMyMatchingHistory_MapsFields_WhenDataExists() {
            List<WorkerMatchingHistoryDto> result = workerMapper.findMyMatchingHistory(workerEmployeeId);
            assumeTrue(!result.isEmpty(), "배정 이력 데이터 없음 — skip");

            WorkerMatchingHistoryDto first = result.get(0);
            assertNotNull(first.getMatchingRecordId(), "matchingRecordId 매핑 확인");
            assertNotNull(first.getOrderNumber(),      "orderNumber 매핑 확인");
            assertNotNull(first.getStatus(),           "status 매핑 확인");
        }
    }

    // ===== findMyTaskSummary =====

    @Nested
    @DisplayName("findMyTaskSummary — 작업 현황 집계 조회")
    class FindMyTaskSummary {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료되고 DTO가 반환된다")
        void findMyTaskSummary_ReturnsDto() {
            WorkerTaskSummaryDto result = workerMapper.findMyTaskSummary(workerEmployeeId);

            assertNotNull(result, "요약 DTO가 null 이면 안 된다");
        }

        @Test
        @DisplayName("집계 값들이 모두 0 이상이다")
        void findMyTaskSummary_CountsAreNonNegative() {
            WorkerTaskSummaryDto result = workerMapper.findMyTaskSummary(workerEmployeeId);
            assumeTrue(result != null, "요약 DTO 없음 — skip");

            assertTrue(result.getAssignedCount()   >= 0, "assignedCount 음수 불가");
            assertTrue(result.getInProgressCount() >= 0, "inProgressCount 음수 불가");
            assertTrue(result.getCompletedCount()  >= 0, "completedCount 음수 불가");
        }

        @Test
        @DisplayName("존재하지 않는 직원 ID로 조회하면 모든 카운트가 0이다")
        void findMyTaskSummary_ReturnsZeroCounts_WhenEmployeeNotExists() {
            WorkerTaskSummaryDto result = workerMapper.findMyTaskSummary(-1L);

            assertNotNull(result, "존재하지 않는 직원 ID도 DTO를 반환해야 한다");
            assertTrue(result.getAssignedCount()   == 0, "데이터 없으면 assignedCount는 0이어야 한다");
            assertTrue(result.getInProgressCount() == 0, "데이터 없으면 inProgressCount는 0이어야 한다");
            assertTrue(result.getCompletedCount()  == 0, "데이터 없으면 completedCount는 0이어야 한다");
        }
    }

    private void assertTrue(boolean condition, String message) {
        org.junit.jupiter.api.Assertions.assertTrue(condition, message);
    }
}
