package com.ohgiraffers.team3backendscm.scm.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.TaskFinishRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.MatchingRecordRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 작업자(Worker) Task 실행 흐름 전체를 검증하는 통합 테스트.
 * Controller → Service → DB 까지 가짜 객체 없이 실제 동작을 확인한다.
 *
 * <p>테스트 전략: @SpringBootTest + @AutoConfigureMockMvc (가이드 5-5 준수)
 * - @Transactional: 각 테스트 후 자동 롤백 → @AfterEach 수동 삭제 불필요.
 * - entityManager.flush(): JPA 쓰기 지연 버퍼를 DB에 강제 반영.
 * - entityManager.clear(): 1차 캐시를 비워 이후 조회가 DB 를 직접 읽도록 강제.
 * </p>
 *
 * <p>검증 시나리오:
 * <ol>
 *   <li>작업 시작 (POST /tasks/{taskId}/start)
 *       → MatchingRecord.workStartAt 이 현재 시각으로 기록된다</li>
 *   <li>작업 임시저장 (POST /tasks/{taskId}/finish-draft)
 *       → workEndAt·comment 기록, MatchingStatus 는 CONFIRM 유지</li>
 *   <li>작업 종료 제출 (POST /tasks/{taskId}/finish)
 *       → MatchingRecord COMPLETE 전환 + Order COMPLETED 전환</li>
 * </ol>
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
class TaskIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MatchingRecordRepository matchingRecordRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @PersistenceContext private EntityManager entityManager;

    private final TimeBasedIdGenerator idGenerator = new TimeBasedIdGenerator();

    private Long testProductId;
    private Long testConfigId;
    private Long testOrderId;
    private Long testMatchingRecordId;
    private Long testEmployeeId;

    /**
     * 각 테스트 전 FK 의존 사전 데이터를 삽입한다.
     * - product, OCSA_weight_config, orders(INPROGRESS), matching_record(CONFIRM) 순으로 삽입.
     * @Transactional 범위 안에서 실행되므로 테스트 후 자동 롤백된다.
     */
    @BeforeEach
    void setUp() {
        testProductId        = idGenerator.generate();
        testConfigId         = idGenerator.generate();
        testOrderId          = idGenerator.generate();
        testMatchingRecordId = idGenerator.generate();
        List<Long> employeeIds = findScmReferencedEmployeeIds();
        assumeTrue(!employeeIds.isEmpty(), "SCM 배정/배치 employee 참조 데이터가 없어 테스트를 건너뜁니다.");
        testEmployeeId = employeeIds.get(0);

        // FK 의존 사전 데이터 삽입
        jdbcTemplate.update(
                "INSERT INTO product (product_id, product_name, product_code) VALUES (?, ?, ?)",
                testProductId, "Task 테스트 제품", "TASK-PROD");

        jdbcTemplate.update(
                "INSERT INTO OCSA_weight_config (config_id, industry_preset_name) VALUES (?, ?)",
                testConfigId, "SEMICONDUCTOR");

        // 작업 대상 주문 삽입 (INPROGRESS 상태 — TaskCommandService.finish() 에서 order.complete() 호출 전제)
        jdbcTemplate.update(
                "INSERT INTO orders (order_id, product_id, config_id, order_no, order_quantity, order_status, order_deadline, process_step_count, tolerance_mm, skill_level, is_first_order) " +
                "VALUES (?, ?, ?, ?, 1, 'INPROGRESS', ?, 1, 0.1000, 1, false)",
                testOrderId, testProductId, testConfigId,
                "ORD-TASK-" + testOrderId,
                LocalDate.now().plusDays(5).toString());

        // 배정 기록 삽입 (CONFIRM 상태)
        jdbcTemplate.update(
                "INSERT INTO matching_record (matching_record_id, order_id, employee_id, matching_status, created_at) " +
                "VALUES (?, ?, ?, 'CONFIRM', NOW())",
                testMatchingRecordId, testOrderId, testEmployeeId);
    }

    // ===== 성공 케이스 =====

    @Test
    @DisplayName("작업 시작 전체 흐름: POST /tasks/{taskId}/start → workStartAt DB 기록")
    void startTask_FullFlow() throws Exception {
        // when - POST /api/v1/scm/workers/me/today-tasks/{taskId}/start
        mockMvc.perform(post("/api/v1/scm/workers/me/today-tasks/" + testMatchingRecordId + "/start"))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        // then - MatchingRecord 의 workStartAt 이 기록되었는지 확인
        MatchingRecord record = matchingRecordRepository.findById(testMatchingRecordId).orElseThrow();
        assertNotNull(record.getWorkStartAt(), "작업 시작 후 workStartAt 이 기록되어야 한다");
        // 상태는 여전히 CONFIRM 유지
        assertEquals(MatchingStatus.CONFIRM, record.getStatus());
    }

    @Test
    @DisplayName("작업 임시저장 전체 흐름: POST /tasks/{taskId}/finish-draft → workEndAt·comment 기록, 상태 유지")
    void finishDraft_FullFlow() throws Exception {
        // given
        TaskFinishRequest request = new TaskFinishRequest("임시 저장 코멘트");

        // when - POST /api/v1/scm/workers/me/today-tasks/{taskId}/finish-draft
        mockMvc.perform(post("/api/v1/scm/workers/me/today-tasks/" + testMatchingRecordId + "/finish-draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        // then - workEndAt 과 comment 가 기록되어야 한다
        MatchingRecord record = matchingRecordRepository.findById(testMatchingRecordId).orElseThrow();
        assertNotNull(record.getWorkEndAt(), "임시저장 후 workEndAt 이 기록되어야 한다");
        assertEquals("임시 저장 코멘트", record.getComment());
        // 임시저장은 상태를 변경하지 않는다
        assertEquals(MatchingStatus.CONFIRM, record.getStatus());
    }

    @Test
    @DisplayName("작업 종료 제출 전체 흐름: POST /tasks/{taskId}/finish → MatchingRecord COMPLETE + 주문 COMPLETED")
    void finish_FullFlow() throws Exception {
        // given
        TaskFinishRequest request = new TaskFinishRequest("최종 완료 코멘트");

        // when - POST /api/v1/scm/workers/me/today-tasks/{taskId}/finish
        mockMvc.perform(post("/api/v1/scm/workers/me/today-tasks/" + testMatchingRecordId + "/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        // then - MatchingRecord 상태가 COMPLETE 로 전환되었는지 확인
        MatchingRecord record = matchingRecordRepository.findById(testMatchingRecordId).orElseThrow();
        assertEquals(MatchingStatus.COMPLETE, record.getStatus());
        assertNotNull(record.getWorkEndAt(), "종료 제출 후 workEndAt 이 기록되어야 한다");
        assertEquals("최종 완료 코멘트", record.getComment());

        // then - 연관 주문 상태가 COMPLETED 로 전환되었는지 확인
        Order order = orderRepository.findById(testOrderId).orElseThrow();
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    // ===== 예외 케이스 =====

    @Nested
    @DisplayName("작업 시작(start) 예외")
    class StartTaskFail {

        @Test
        @DisplayName("존재하지 않는 작업 ID로 시작 요청 시 404를 반환한다")
        void startTask_Fail_WhenNotFound() throws Exception {
            mockMvc.perform(post("/api/v1/scm/workers/me/today-tasks/-1/start"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("이미 시작된 작업을 다시 시작 요청 시 400을 반환한다")
        void startTask_Fail_WhenAlreadyStarted() throws Exception {
            // 첫 번째 시작 — 성공
            mockMvc.perform(post("/api/v1/scm/workers/me/today-tasks/" + testMatchingRecordId + "/start"))
                    .andExpect(status().isOk());

            entityManager.flush();
            entityManager.clear();

            // 두 번째 시작 — MatchingRecord.startWork() 도메인 예외 발생
            mockMvc.perform(post("/api/v1/scm/workers/me/today-tasks/" + testMatchingRecordId + "/start"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("작업 임시저장(finish-draft) 예외")
    class FinishDraftFail {

        @Test
        @DisplayName("존재하지 않는 작업 ID로 임시저장 요청 시 404를 반환한다")
        void finishDraft_Fail_WhenNotFound() throws Exception {
            TaskFinishRequest request = new TaskFinishRequest("코멘트");

            mockMvc.perform(post("/api/v1/scm/workers/me/today-tasks/-1/finish-draft")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("작업 종료 제출(finish) 예외")
    class FinishFail {

        @Test
        @DisplayName("존재하지 않는 작업 ID로 종료 제출 시 404를 반환한다")
        void finish_Fail_WhenNotFound() throws Exception {
            TaskFinishRequest request = new TaskFinishRequest("코멘트");

            mockMvc.perform(post("/api/v1/scm/workers/me/today-tasks/-1/finish")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("이미 COMPLETE 상태인 배정 기록에 종료 제출 시 400을 반환한다")
        void finish_Fail_WhenAlreadyComplete() throws Exception {
            // 먼저 정상 종료 처리
            mockMvc.perform(post("/api/v1/scm/workers/me/today-tasks/" + testMatchingRecordId + "/finish")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new TaskFinishRequest("1차 완료"))))
                    .andExpect(status().isOk());

            entityManager.flush();
            entityManager.clear();

            // COMPLETE 상태에서 finish 재시도 — order.complete() 도메인 예외 발생
            mockMvc.perform(post("/api/v1/scm/workers/me/today-tasks/" + testMatchingRecordId + "/finish")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new TaskFinishRequest("2차 시도"))))
                    .andExpect(status().isBadRequest());
        }
    }

    private List<Long> findScmReferencedEmployeeIds() {
        return jdbcTemplate.queryForList("""
                SELECT employee_id
                  FROM (
                        SELECT employee_id FROM worker_deployment WHERE employee_id IS NOT NULL
                        UNION
                        SELECT employee_id FROM matching_record WHERE employee_id IS NOT NULL
                       ) scm_employee_refs
                 LIMIT 1
                """, Long.class);
    }
}
