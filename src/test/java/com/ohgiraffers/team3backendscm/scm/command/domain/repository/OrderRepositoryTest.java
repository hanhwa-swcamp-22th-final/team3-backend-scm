package com.ohgiraffers.team3backendscm.scm.command.domain.repository;

import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OrderRepository 커스텀 쿼리 및 JPA 영속성을 검증하는 데이터 계층 테스트.
 *
 * <p>테스트 전략: @DataJpaTest (가이드 5-2 준수)
 * - JPA 슬라이스만 로드하여 Service, Controller 빈 없이 가볍게 실행한다.
 * - @DataJpaTest 가 기본으로 @Transactional 을 적용하므로, 테스트 종료 후 자동 롤백된다.
 *   → @AfterEach 수동 삭제 불필요.
 * - @AutoConfigureTestDatabase(replace = NONE): H2 대신 실제 MySQL 데이터소스 사용.
 * - JPA Auditing(@Import 불필요): findById 는 읽기 전용이고 JdbcTemplate 으로 삽입하므로
 *   AuditingEntityListener 가 실행되지 않는다. AuditorAwareImpl 로드 불필요.
 * </p>
 *
 * <p>참고: OrderRepository 는 커스텀 메서드가 없으므로, 여기서는
 * JPA 기본 save/findById 가 실제 DB 스키마(FK 포함)에서 정상 동작하는지
 * 통합 영속성을 확인하는 목적으로만 사용한다.
 * </p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final TimeBasedIdGenerator idGenerator = new TimeBasedIdGenerator();

    private Long testProductId;
    private Long testConfigId;
    private Long testOrderId;

    /**
     * 각 테스트 실행 전 FK 의존 사전 데이터(product, OCSA_weight_config)와
     * 테스트 대상 주문(orders)을 JdbcTemplate 으로 삽입한다.
     * @DataJpaTest 의 @Transactional 범위 안에서 실행되므로 테스트 후 자동 롤백된다.
     */
    @BeforeEach
    void setUp() {
        testProductId = idGenerator.generate();
        testConfigId  = idGenerator.generate();
        testOrderId   = idGenerator.generate();

        // orders 테이블의 FK 의존 데이터 먼저 삽입
        jdbcTemplate.update(
                "INSERT INTO product (product_id, product_name, product_code) VALUES (?, ?, ?)",
                testProductId, "테스트 제품", "TEST-PROD");

        jdbcTemplate.update(
                "INSERT INTO OCSA_weight_config (config_id, industry_preset) VALUES (?, ?)",
                testConfigId, "SEMICONDUCTOR");

        // 테스트 대상 주문 삽입
        jdbcTemplate.update(
                "INSERT INTO orders (order_id, product_id, config_id, order_no, order_quantity, order_status, order_deadline) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                testOrderId, testProductId, testConfigId,
                "ORD-TEST-" + testOrderId, 1, "ANALYZED",
                LocalDate.now().plusDays(5).toString());
    }

    // @AfterEach 불필요: @DataJpaTest 가 테스트별 트랜잭션을 자동 롤백한다.

    @Test
    @DisplayName("저장한 주문을 ID로 다시 조회할 수 있다")
    void findById_ReturnsOrder() {
        // when - 사전 삽입된 주문을 JPA로 조회
        Optional<Order> found = orderRepository.findById(testOrderId);

        // then - 존재 여부 및 상태값 검증
        assertTrue(found.isPresent());
        assertEquals(OrderStatus.ANALYZED, found.get().getStatus());
    }
}
