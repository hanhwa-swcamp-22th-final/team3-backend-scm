package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.TaskDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerMatchingHistoryDto;
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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * WorkerMapper (workers.xml) MyBatis 쿼리 통합 테스트.
 *
 * <p>테스트 전략: @SpringBootTest — 실제 DB에 대해 SQL 바인딩·ResultMap 매핑 오류를 검증한다.
 * WORKER 역할 직원이 없으면 assumeTrue 로 건너뛴다.
 * </p>
 */
@SpringBootTest
@Transactional
@DisplayName("WorkerMapper XML 쿼리 테스트")
class WorkerMapperTest {

    @Autowired
    private WorkerMapper workerMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** DB에 존재하는 WORKER 역할 직원 ID. 없으면 null. */
    private Long workerEmployeeId;

    @BeforeEach
    void setUp() {
        workerEmployeeId = jdbcTemplate.queryForObject(
                "SELECT employee_id FROM employee WHERE employee_role = 'WORKER' LIMIT 1",
                Long.class);
    }

    // ===== findMyPendingTasks =====

    @Nested
    @DisplayName("findMyPendingTasks — 미완료 작업 목록 조회")
    class FindMyPendingTasks {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
        void findMyPendingTasks_ExecutesWithoutError() {
            assumeTrue(workerEmployeeId != null, "WORKER 직원 데이터 없음 — skip");

            List<TaskDto> result = workerMapper.findMyPendingTasks(workerEmployeeId);

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("REJECT·COMPLETE 상태 작업은 결과에 포함되지 않는다")
        void findMyPendingTasks_ExcludesRejectedAndCompleted() {
            assumeTrue(workerEmployeeId != null, "WORKER 직원 데이터 없음 — skip");

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
            assumeTrue(workerEmployeeId != null, "WORKER 직원 데이터 없음 — skip");

            List<WorkerDeploymentDto> result = workerMapper.findMyDeployments(workerEmployeeId);

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 facilityId·deploymentDate 필드가 매핑된다")
        void findMyDeployments_MapsFields_WhenDataExists() {
            assumeTrue(workerEmployeeId != null, "WORKER 직원 데이터 없음 — skip");

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
            assumeTrue(workerEmployeeId != null, "WORKER 직원 데이터 없음 — skip");

            List<WorkerMatchingHistoryDto> result = workerMapper.findMyMatchingHistory(workerEmployeeId);

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("데이터가 있으면 matchingRecordId·orderNumber·status 필드가 매핑된다")
        void findMyMatchingHistory_MapsFields_WhenDataExists() {
            assumeTrue(workerEmployeeId != null, "WORKER 직원 데이터 없음 — skip");

            List<WorkerMatchingHistoryDto> result = workerMapper.findMyMatchingHistory(workerEmployeeId);
            assumeTrue(!result.isEmpty(), "배정 이력 데이터 없음 — skip");

            WorkerMatchingHistoryDto first = result.get(0);
            assertNotNull(first.getMatchingRecordId(), "matchingRecordId 매핑 확인");
            assertNotNull(first.getOrderNumber(),      "orderNumber 매핑 확인");
            assertNotNull(first.getStatus(),           "status 매핑 확인");
        }
    }

    private void assertTrue(boolean condition, String message) {
        org.junit.jupiter.api.Assertions.assertTrue(condition, message);
    }
}
