package com.ohgiraffers.team3backendscm.scm.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.AssignRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ReassignRequest;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 배정(Assignment) 전체 흐름을 검증하는 통합 테스트.
 * Controller → Service → DB 까지 가짜 객체 없이 실제 동작을 확인한다.
 *
 * <p>테스트 전략: @SpringBootTest + @AutoConfigureMockMvc (가이드 5-5 준수)
 * - @Transactional: 각 테스트 메서드 실행 후 트랜잭션을 자동 롤백하여 DB 상태를 원복한다.
 *   MockMvc(MOCK 환경)는 동일 스레드에서 동기 실행되므로, Service 의 @Transactional(REQUIRED)
 *   이 테스트 트랜잭션에 참여하여 함께 롤백된다. → @AfterEach 수동 삭제 불필요.
 * - entityManager.flush(): MockMvc 호출 후 JPA 쓰기 지연 버퍼를 DB에 반영한다.
 * - entityManager.clear(): 1차 캐시를 비워 이후 조회가 DB를 직접 읽도록 강제한다.
 * </p>
 *
 * <p>검증 시나리오:
 * <ol>
 *   <li>배정 확정 (POST /assignments)  → 주문 INPROGRESS + MatchingRecord 저장</li>
 *   <li>기술자 재배정 (PUT /assignments/{id})  → MatchingRecord.employeeId 변경</li>
 *   <li>배정 취소 (DELETE /assignments/{id}) → MatchingRecord REJECT + 주문 ANALYZED 롤백</li>
 * </ol>
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AssignmentIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OrderRepository orderRepository;
    @Autowired private MatchingRecordRepository matchingRecordRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @PersistenceContext private EntityManager entityManager;

    private final TimeBasedIdGenerator idGenerator = new TimeBasedIdGenerator();

    private Long testProductId;
    private Long testConfigId;
    private Long testOrderId;
    private Long testEmployeeId;

    /**
     * 각 테스트 전 FK 의존 사전 데이터와 테스트 주문을 삽입하고,
     * 실제 employee 테이블에서 기술자 ID를 조회한다.
     * employee 가 없으면 테스트를 건너뛴다 (assumeTrue).
     * @Transactional 범위 안에서 실행되므로 테스트 후 자동 롤백된다.
     */
    @BeforeEach
    void setUp() {
        testProductId = idGenerator.generate();
        testConfigId  = idGenerator.generate();
        testOrderId   = idGenerator.generate();

        // 실제 DB의 employee_id 조회 (없으면 테스트 skip)
        List<Long> employees = jdbcTemplate.queryForList(
                "SELECT employee_id FROM employee LIMIT 1", Long.class);
        assumeTrue(!employees.isEmpty(), "employee 데이터가 없어 테스트를 건너뜁니다.");
        testEmployeeId = employees.get(0);

        // FK 의존 사전 데이터 삽입
        jdbcTemplate.update(
                "INSERT INTO product (product_id, product_name, product_code) VALUES (?, ?, ?)",
                testProductId, "테스트 제품", "TEST-PROD");

        jdbcTemplate.update(
                "INSERT INTO OCSA_weight_config (config_id, industry_preset) VALUES (?, ?)",
                testConfigId, "SEMICONDUCTOR");

        // 배정 대상 주문 삽입 (ANALYZED 상태)
        jdbcTemplate.update(
                "INSERT INTO orders (order_id, product_id, config_id, order_no, order_quantity, order_status, order_deadline) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                testOrderId, testProductId, testConfigId,
                "ORD-TEST-" + testOrderId, 1, "ANALYZED",
                LocalDate.now().plusDays(5).toString());
    }

    // @AfterEach 불필요: @Transactional 이 테스트 종료 후 자동 롤백을 보장한다.

    // ===== 성공 케이스 =====

    @Test
    @DisplayName("배정 전체 흐름: API 호출 → 주문 상태 INPROGRESS 전환 → MatchingRecord DB 저장")
    void assign_FullFlow() throws Exception {
        // given - 배정 요청 DTO (orderId + technicianId, matchingMode 는 서비스에서 자동 결정)
        AssignRequest request = new AssignRequest(testOrderId, testEmployeeId);

        // when - POST /api/v1/scm/assignments 호출 (Service 트랜잭션이 테스트 트랜잭션에 참여)
        mockMvc.perform(post("/api/v1/scm/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // JPA 쓰기 지연 버퍼를 DB에 반영하고, 1차 캐시를 비워 아래 조회가 DB를 직접 읽도록 한다
        entityManager.flush();
        entityManager.clear();

        // then - 주문 상태가 INPROGRESS 로 전환되었는지 확인
        Order updated = orderRepository.findById(testOrderId).orElseThrow();
        assertEquals(OrderStatus.INPROGRESS, updated.getStatus());

        // then - MatchingRecord 가 DB에 저장되었는지 확인
        List<MatchingRecord> records =
                matchingRecordRepository.findByTechnicianIdAndAssignedDate(testEmployeeId, LocalDate.now());
        assertFalse(records.isEmpty(), "배정 후 MatchingRecord 가 저장되어 있어야 한다");
        MatchingRecord saved = records.get(0);
        assertEquals(testEmployeeId, saved.getEmployeeId(), "저장된 배정 기록의 기술자 ID 가 일치해야 한다");
        assertEquals(testOrderId,    saved.getOrderId(),    "저장된 배정 기록의 주문 ID 가 일치해야 한다");
    }

    @Test
    @DisplayName("재배정 전체 흐름: PUT → MatchingRecord.employeeId 변경 → DB 반영")
    void reassign_FullFlow() throws Exception {
        // given - INPROGRESS 주문과 CONFIRM 배정 기록 삽입
        Long inprogressOrderId = idGenerator.generate();
        Long matchingRecordId  = idGenerator.generate();

        jdbcTemplate.update(
                "INSERT INTO orders (order_id, product_id, config_id, order_no, order_quantity, order_status, order_deadline) " +
                "VALUES (?, ?, ?, ?, 1, 'INPROGRESS', ?)",
                inprogressOrderId, testProductId, testConfigId,
                "ORD-REASSIGN-" + inprogressOrderId,
                LocalDate.now().plusDays(5).toString());

        jdbcTemplate.update(
                "INSERT INTO matching_record (matching_record_id, order_id, employee_id, matching_status, created_at) " +
                "VALUES (?, ?, ?, 'CONFIRM', NOW())",
                matchingRecordId, inprogressOrderId, testEmployeeId);

        // 재배정 대상: 현재 기술자와 다른 employee 조회 (없으면 skip)
        List<Long> others = jdbcTemplate.queryForList(
                "SELECT employee_id FROM employee WHERE employee_id != ? LIMIT 1",
                Long.class, testEmployeeId);
        assumeTrue(!others.isEmpty(), "재배정 테스트를 위한 다른 employee 가 없어 건너뜁니다.");
        Long newEmployeeId = others.get(0);

        // when - PUT /api/v1/scm/assignments/{matchingRecordId}
        ReassignRequest request = new ReassignRequest(newEmployeeId);
        mockMvc.perform(put("/api/v1/scm/assignments/" + matchingRecordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        // then - MatchingRecord 의 employeeId 가 새 기술자로 변경되었는지 확인
        MatchingRecord updated = matchingRecordRepository.findById(matchingRecordId).orElseThrow();
        assertEquals(newEmployeeId, updated.getEmployeeId());
    }

    @Test
    @DisplayName("배정 취소 전체 흐름: DELETE → MatchingRecord REJECT + 주문 ANALYZED 롤백 → DB 반영")
    void cancel_FullFlow() throws Exception {
        // given - INPROGRESS 주문과 CONFIRM 배정 기록 삽입
        Long inprogressOrderId = idGenerator.generate();
        Long matchingRecordId  = idGenerator.generate();

        jdbcTemplate.update(
                "INSERT INTO orders (order_id, product_id, config_id, order_no, order_quantity, order_status, order_deadline) " +
                "VALUES (?, ?, ?, ?, 1, 'INPROGRESS', ?)",
                inprogressOrderId, testProductId, testConfigId,
                "ORD-CANCEL-" + inprogressOrderId,
                LocalDate.now().plusDays(5).toString());

        jdbcTemplate.update(
                "INSERT INTO matching_record (matching_record_id, order_id, employee_id, matching_status, created_at) " +
                "VALUES (?, ?, ?, 'CONFIRM', NOW())",
                matchingRecordId, inprogressOrderId, testEmployeeId);

        // when - DELETE /api/v1/scm/assignments/{matchingRecordId}
        mockMvc.perform(delete("/api/v1/scm/assignments/" + matchingRecordId))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        // then - MatchingRecord 상태가 REJECT 로 변경되었는지 확인
        MatchingRecord cancelled = matchingRecordRepository.findById(matchingRecordId).orElseThrow();
        assertEquals(MatchingStatus.REJECT, cancelled.getStatus());

        // then - 주문 상태가 ANALYZED 로 롤백되었는지 확인
        Order rolledBack = orderRepository.findById(inprogressOrderId).orElseThrow();
        assertEquals(OrderStatus.ANALYZED, rolledBack.getStatus());
    }

    // ===== 예외 케이스 =====

    @Nested
    @DisplayName("배정(assign) 예외")
    class AssignFail {

        @Test
        @DisplayName("존재하지 않는 주문 ID로 배정 요청 시 404를 반환한다")
        void assign_Fail_WhenOrderNotFound() throws Exception {
            AssignRequest request = new AssignRequest(-1L, testEmployeeId);

            mockMvc.perform(post("/api/v1/scm/assignments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("ANALYZED 상태가 아닌 주문(REGISTERED)에 배정 요청 시 400을 반환한다")
        void assign_Fail_WhenNotAnalyzed() throws Exception {
            // REGISTERED 상태 주문 별도 삽입
            Long registeredOrderId = idGenerator.generate();
            jdbcTemplate.update(
                    "INSERT INTO orders (order_id, product_id, config_id, order_no, order_quantity, order_status, order_deadline) " +
                    "VALUES (?, ?, ?, ?, 1, 'REGISTERED', ?)",
                    registeredOrderId, testProductId, testConfigId,
                    "ORD-REG-" + registeredOrderId,
                    LocalDate.now().plusDays(5).toString());

            AssignRequest request = new AssignRequest(registeredOrderId, testEmployeeId);

            mockMvc.perform(post("/api/v1/scm/assignments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("재배정(reassign) 예외")
    class ReassignFail {

        @Test
        @DisplayName("존재하지 않는 배정 ID로 재배정 요청 시 404를 반환한다")
        void reassign_Fail_WhenRecordNotFound() throws Exception {
            ReassignRequest request = new ReassignRequest(testEmployeeId);

            mockMvc.perform(put("/api/v1/scm/assignments/-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("배정 취소(cancel) 예외")
    class CancelFail {

        @Test
        @DisplayName("존재하지 않는 배정 ID로 취소 요청 시 404를 반환한다")
        void cancel_Fail_WhenRecordNotFound() throws Exception {
            mockMvc.perform(delete("/api/v1/scm/assignments/-1"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("이미 COMPLETE 상태인 배정 취소 요청 시 400을 반환한다")
        void cancel_Fail_WhenAlreadyComplete() throws Exception {
            // COMPLETE 상태 배정 기록 삽입
            Long completedOrderId  = idGenerator.generate();
            Long completedRecordId = idGenerator.generate();

            jdbcTemplate.update(
                    "INSERT INTO orders (order_id, product_id, config_id, order_no, order_quantity, order_status, order_deadline) " +
                    "VALUES (?, ?, ?, ?, 1, 'COMPLETED', ?)",
                    completedOrderId, testProductId, testConfigId,
                    "ORD-DONE-" + completedOrderId,
                    LocalDate.now().plusDays(5).toString());

            jdbcTemplate.update(
                    "INSERT INTO matching_record (matching_record_id, order_id, employee_id, matching_status, created_at) " +
                    "VALUES (?, ?, ?, 'COMPLETE', NOW())",
                    completedRecordId, completedOrderId, testEmployeeId);

            mockMvc.perform(delete("/api/v1/scm/assignments/" + completedRecordId))
                    .andExpect(status().isBadRequest());
        }
    }
}
