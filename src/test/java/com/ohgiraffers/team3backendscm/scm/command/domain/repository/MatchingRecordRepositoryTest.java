package com.ohgiraffers.team3backendscm.scm.command.domain.repository;

import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingMode;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * MatchingRecordRepository 커스텀 쿼리 단위 테스트.
 *
 * <p>테스트 전략: @SpringBootTest + @Transactional — 실제 DB 에서 JPQL 쿼리의
 * 날짜 함수 바인딩과 기술자 ID 필터가 의도대로 동작하는지 검증한다.
 * </p>
 *
 * <p>검증 대상: {@link MatchingRecordRepository#findByTechnicianIdAndAssignedDate}
 * <ul>
 *   <li>당일 기록 반환 (정상 케이스)</li>
 *   <li>다른 기술자 ID로는 조회되지 않음</li>
 *   <li>다른 날짜로는 조회되지 않음</li>
 *   <li>기록 없으면 빈 리스트 반환</li>
 * </ul>
 * </p>
 */
@SpringBootTest
@Transactional
@DisplayName("MatchingRecordRepository 커스텀 쿼리 테스트")
class MatchingRecordRepositoryTest {

    @Autowired
    private MatchingRecordRepository matchingRecordRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

        List<Long> employees = jdbcTemplate.queryForList(
                "SELECT employee_id FROM employee LIMIT 1", Long.class);
        assumeTrue(!employees.isEmpty(), "employee 데이터가 없어 테스트를 건너뜁니다.");
        testEmployeeId = employees.get(0);

        jdbcTemplate.update(
                "INSERT INTO product (product_id, product_name, product_code) VALUES (?, ?, ?)",
                testProductId, "테스트 제품", "TEST-REPO");

        jdbcTemplate.update(
                "INSERT INTO OCSA_weight_config (config_id, industry_preset) VALUES (?, ?)",
                testConfigId, "SEMICONDUCTOR");

        jdbcTemplate.update(
                "INSERT INTO orders (order_id, product_id, config_id, order_no, order_quantity, order_status, order_deadline) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                testOrderId, testProductId, testConfigId,
                "ORD-REPO-" + testOrderId, 1, "ANALYZED",
                LocalDate.now().plusDays(5).toString());
    }

    @Nested
    @DisplayName("findByTechnicianIdAndAssignedDate — 기술자·날짜 기준 배정 이력 조회")
    class FindByTechnicianIdAndAssignedDate {

        @Test
        @DisplayName("당일 저장된 배정 기록이 조회된다")
        void returnsRecord_WhenSavedToday() {
            // given
            MatchingRecord record = new MatchingRecord(
                    idGenerator.generate(), testOrderId, testEmployeeId, MatchingMode.EFFICIENCY_TYPE);
            matchingRecordRepository.save(record);

            // when
            List<MatchingRecord> result = matchingRecordRepository
                    .findByTechnicianIdAndAssignedDate(testEmployeeId, LocalDate.now());

            // then
            assertTrue(result.stream().anyMatch(r -> r.getOrderId().equals(testOrderId)),
                    "저장한 배정 기록이 조회되어야 한다");
        }

        @Test
        @DisplayName("다른 기술자 ID로 조회하면 해당 기록이 포함되지 않는다")
        void excludesRecord_WhenDifferentTechnicianId() {
            // given
            MatchingRecord record = new MatchingRecord(
                    idGenerator.generate(), testOrderId, testEmployeeId, MatchingMode.EFFICIENCY_TYPE);
            matchingRecordRepository.save(record);

            Long otherEmployeeId = -999L; // 존재하지 않는 ID

            // when
            List<MatchingRecord> result = matchingRecordRepository
                    .findByTechnicianIdAndAssignedDate(otherEmployeeId, LocalDate.now());

            // then
            assertTrue(result.stream().noneMatch(r -> r.getOrderId().equals(testOrderId)),
                    "다른 기술자 ID로 조회하면 해당 기록이 반환되면 안 된다");
        }

        @Test
        @DisplayName("어제 날짜로 조회하면 오늘 저장된 기록이 포함되지 않는다")
        void excludesRecord_WhenDifferentDate() {
            // given
            MatchingRecord record = new MatchingRecord(
                    idGenerator.generate(), testOrderId, testEmployeeId, MatchingMode.EFFICIENCY_TYPE);
            matchingRecordRepository.save(record);

            // when — 어제 날짜로 조회
            List<MatchingRecord> result = matchingRecordRepository
                    .findByTechnicianIdAndAssignedDate(testEmployeeId, LocalDate.now().minusDays(1));

            // then
            assertTrue(result.stream().noneMatch(r -> r.getOrderId().equals(testOrderId)),
                    "어제 날짜로 조회하면 오늘 저장된 기록이 반환되면 안 된다");
        }

        @Test
        @DisplayName("배정 기록이 없으면 빈 리스트를 반환한다")
        void returnsEmptyList_WhenNoRecord() {
            // when — 존재하지 않는 기술자 ID로 조회
            List<MatchingRecord> result = matchingRecordRepository
                    .findByTechnicianIdAndAssignedDate(-1L, LocalDate.now());

            // then
            assertTrue(result.isEmpty(), "배정 기록이 없으면 빈 리스트여야 한다");
        }
    }
}
