package com.ohgiraffers.team3backendscm.scm.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.AssignRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.MatchingRecordRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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

        jdbcTemplate.update(
                "INSERT INTO product (product_id, product_name, product_code) VALUES (?, ?, ?)",
                testProductId, "테스트 제품", "TEST-PROD");

        jdbcTemplate.update(
                "INSERT INTO OCSA_weight_config (config_id, industry_preset) VALUES (?, ?)",
                testConfigId, "SEMICONDUCTOR");

        jdbcTemplate.update(
                "INSERT INTO orders (order_id, product_id, config_id, order_no, order_quantity, order_status, order_deadline) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                testOrderId, testProductId, testConfigId,
                "ORD-TEST-" + testOrderId, 1, "ANALYZED",
                LocalDate.now().plusDays(5).toString());
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM matching_record WHERE order_id = ?", testOrderId);
        jdbcTemplate.update("DELETE FROM orders WHERE order_id = ?", testOrderId);
        jdbcTemplate.update("DELETE FROM OCSA_weight_config WHERE config_id = ?", testConfigId);
        jdbcTemplate.update("DELETE FROM product WHERE product_id = ?", testProductId);
    }

    @Test
    @DisplayName("배정 전체 흐름: API 호출 → 주문 상태 INPROGRESS 전환 → MatchingRecord DB 저장")
    void assign_FullFlow() throws Exception {
        // given
        AssignRequest request = new AssignRequest(testOrderId, testEmployeeId);

        // when
        mockMvc.perform(post("/api/v1/scm/task-matching/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 서비스 트랜잭션 커밋 후 1차 캐시 초기화
        entityManager.clear();

        // then - 주문 상태 INPROGRESS 전환 확인
        Order updated = orderRepository.findById(testOrderId).orElseThrow();
        assertEquals(OrderStatus.INPROGRESS, updated.getStatus());

        // then - MatchingRecord DB 저장 확인
        List<MatchingRecord> records =
                matchingRecordRepository.findByTechnicianIdAndAssignedDate(testEmployeeId, LocalDate.now());
        assertFalse(records.isEmpty());
    }
}
