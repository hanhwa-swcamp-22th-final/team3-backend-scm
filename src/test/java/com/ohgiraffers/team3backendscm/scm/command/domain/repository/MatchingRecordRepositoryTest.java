package com.ohgiraffers.team3backendscm.scm.command.domain.repository;

import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.MatchingRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * MatchingRecordRepository 의 커스텀 JPQL 쿼리를 검증하는 데이터 계층 테스트.
 *
 * <p>테스트 전략: @DataJpaTest (가이드 5-2 준수)
 * - @DataJpaTest 가 기본으로 @Transactional 을 적용하므로 테스트 후 자동 롤백된다.
 * - @AutoConfigureTestDatabase(replace = NONE): 실제 MySQL 데이터소스 사용.
 * </p>
 *
 * <p>셋업 전략: JdbcTemplate 직접 삽입 사용 이유
 * - MatchingRecord 는 order_id(→ orders → product/config), employee_id(→ HR employee) FK 체인이 있어
 *   JPA save() 단독으로는 FK 제약 위반 발생.
 * - @DataJpaTest 에서 JPA Auditing(@CreatedDate) 이 비활성화되어 JPA save() 시 createdAt = null →
 *   커스텀 쿼리의 FUNCTION('DATE', m.createdAt) 비교가 실패하는 문제도 있음.
 * - JdbcTemplate 으로 FK 의존 데이터와 matching_record 를 직접 삽입하고
 *   created_at 에 명시적 값을 지정하면 두 문제를 모두 해결할 수 있다.
 * - employee 는 HR 도메인이므로 실제 DB에서 조회하여 사용하고, 없으면 assumeTrue 로 skip 한다.
 * </p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MatchingRecordRepositoryTest {

    @Autowired
    private MatchingRecordRepository matchingRecordRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final TimeBasedIdGenerator idGenerator = new TimeBasedIdGenerator();

    // 각 테스트에서 공유할 셋업 데이터 ID
    private Long testEmployeeId;
    private Long testProductId;
    private Long testConfigId;
    private Long testOrderId;
    private Long testRecordId;

    /**
     * FK 의존 사전 데이터와 오늘 날짜의 배정 기록을 JdbcTemplate 으로 삽입한다.
     * @DataJpaTest 의 @Transactional 범위 안에서 실행되므로 테스트 후 자동 롤백된다.
     * employee 데이터가 없으면 테스트를 건너뛴다 (assumeTrue).
     */
    @BeforeEach
    void setUp() {
        // HR 도메인 employee 는 JPA로 생성 불가 → 실제 DB에서 조회
        List<Long> employees = jdbcTemplate.queryForList(
                "SELECT employee_id FROM employee LIMIT 1", Long.class);
        assumeTrue(!employees.isEmpty(), "employee 데이터가 없어 테스트를 건너뜁니다.");
        testEmployeeId = employees.get(0);

        testProductId = idGenerator.generate();
        testConfigId  = idGenerator.generate();
        testOrderId   = idGenerator.generate();
        testRecordId  = idGenerator.generate();

        // matching_record 의 FK 의존 데이터 삽입
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
                "ORD-TEST-" + testOrderId, 1, "INPROGRESS",
                LocalDate.now().plusDays(5).toString());

        // matching_record 삽입 — created_at 명시 (JPA Auditing 없이 직접 지정)
        jdbcTemplate.update(
                "INSERT INTO matching_record (matching_record_id, employee_id, order_id, matching_mode, matching_status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, NOW())",
                testRecordId, testEmployeeId, testOrderId, "EFFICIENCY_TYPE", "CONFIRM");
    }

    @Test
    @DisplayName("오늘 배정된 기술자를 날짜로 조회하면 해당 기록이 반환된다")
    void findByTechnicianAndDate_ReturnsRecord_WhenExists() {
        // when - 오늘 날짜로 setUp 에서 삽입한 기술자의 배정 이력 조회
        List<MatchingRecord> result =
                matchingRecordRepository.findByTechnicianIdAndAssignedDate(testEmployeeId, LocalDate.now());

        // then - 삽입한 배정 기록이 정상 조회되어야 한다
        assertFalse(result.isEmpty(), "오늘 배정된 기록이 존재해야 한다");
        MatchingRecord found = result.get(0);
        assertEquals(testRecordId,   found.getMatchingRecordId(), "반환된 레코드 PK 가 setUp 에서 삽입한 것과 일치해야 한다");
        assertEquals(testEmployeeId, found.getEmployeeId(),       "반환된 레코드의 기술자 ID 가 일치해야 한다");
        assertEquals(testOrderId,    found.getOrderId(),          "반환된 레코드의 주문 ID 가 일치해야 한다");
    }

    @Test
    @DisplayName("존재하지 않는 기술자(-1) 조회 시 빈 리스트를 반환한다")
    void findByTechnicianAndDate_ReturnsEmpty_WhenNotExists() {
        // when - 존재하지 않는 기술자 ID 로 조회
        List<MatchingRecord> result =
                matchingRecordRepository.findByTechnicianIdAndAssignedDate(-1L, LocalDate.now());

        // then - 빈 리스트를 반환해야 한다
        assertTrue(result.isEmpty(), "존재하지 않는 기술자의 배정 이력은 빈 리스트여야 한다");
    }

    @Test
    @DisplayName("같은 기술자라도 다른 날짜로 조회하면 빈 리스트를 반환한다")
    void findByTechnicianAndDate_ReturnsEmpty_WhenDifferentDate() {
        // setUp 에서 오늘(NOW())로 삽입했으므로 어제 날짜로 조회하면 나와서는 안 된다
        // → 쿼리의 DATE(created_at) = :assignedDate 필터가 실제로 작동하는지 검증
        List<MatchingRecord> result =
                matchingRecordRepository.findByTechnicianIdAndAssignedDate(
                        testEmployeeId, LocalDate.now().minusDays(1));

        assertTrue(result.isEmpty(), "다른 날짜의 배정 기록은 조회되지 않아야 한다");
    }
}
