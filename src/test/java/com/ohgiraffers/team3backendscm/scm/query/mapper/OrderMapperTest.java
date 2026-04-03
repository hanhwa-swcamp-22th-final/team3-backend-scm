package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.DifficultyGrade;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.query.dto.request.OrderQueryRequest;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OcsaSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderOcsaDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderReadDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderSummaryDto;
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
 * OrderMapper (orders.xml) MyBatis 쿼리 통합 테스트.
 *
 * <p>테스트 전략: @SpringBootTest — 실제 DB에 대해 SQL 바인딩·ResultMap 매핑 오류를 검증한다.
 * </p>
 */
@SpringBootTest
@Transactional
@DisplayName("OrderMapper XML 쿼리 테스트")
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long validOrderId;

    @BeforeEach
    void setUp() {
        // queryForObject 는 결과 없을 때 예외를 던지므로 queryForList 로 안전하게 조회한다
        List<Long> ids = jdbcTemplate.queryForList("SELECT order_id FROM orders LIMIT 1", Long.class);
        validOrderId = ids.isEmpty() ? null : ids.get(0);
    }

    // ===== findOrders =====

    @Nested
    @DisplayName("findOrders — 주문 목록 조회 (필터·페이징)")
    class FindOrders {

        @Test
        @DisplayName("조건 없이 전체 조회하면 SQL 오류 없이 리스트가 반환된다")
        void findOrders_NoFilter_ReturnsListWithoutError() {
            OrderQueryRequest request = new OrderQueryRequest();

            List<OrderReadDto> result = orderMapper.findOrders(request);

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("status 필터를 지정하면 해당 상태의 주문만 반환된다")
        void findOrders_WithStatusFilter_ReturnsFilteredList() {
            OrderQueryRequest request = new OrderQueryRequest();
            request.setStatus(OrderStatus.REGISTERED);

            List<OrderReadDto> result = orderMapper.findOrders(request);

            assertNotNull(result);
            result.forEach(dto ->
                    assertTrue("REGISTERED".equals(dto.getStatus().name()),
                            "상태 필터 결과에 다른 상태가 포함되면 안 된다"));
        }

        @Test
        @DisplayName("keyword 필터를 지정하면 SQL 오류 없이 실행된다")
        void findOrders_WithKeywordFilter_ExecutesWithoutError() {
            OrderQueryRequest request = new OrderQueryRequest();
            request.setKeyword("ORD");

            List<OrderReadDto> result = orderMapper.findOrders(request);

            assertNotNull(result);
        }

        @Test
        @DisplayName("페이징 파라미터를 지정하면 LIMIT·OFFSET 이 적용된다")
        void findOrders_WithPaging_LimitsResult() {
            OrderQueryRequest allRequest = new OrderQueryRequest();
            List<OrderReadDto> all = orderMapper.findOrders(allRequest);
            assumeTrue(all.size() > 1, "페이징 검증을 위한 데이터 부족 — skip");

            OrderQueryRequest pagedRequest = new OrderQueryRequest();
            pagedRequest.setPage(0);
            pagedRequest.setSize(1);

            List<OrderReadDto> paged = orderMapper.findOrders(pagedRequest);

            assertTrue(paged.size() <= 1, "size=1 페이징 결과는 최대 1건이어야 한다");
        }

        @Test
        @DisplayName("difficultyGrade 필터를 지정하면 SQL 오류 없이 실행된다")
        void findOrders_WithDifficultyGradeFilter_ExecutesWithoutError() {
            OrderQueryRequest request = new OrderQueryRequest();
            request.setDifficultyGrade(DifficultyGrade.D3);

            List<OrderReadDto> result = orderMapper.findOrders(request);

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }
    }

    // ===== findUrgentOrders =====

    @Nested
    @DisplayName("findUrgentOrders — 긴급 주문 목록 조회 (납기 3일 이내)")
    class FindUrgentOrders {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
        void findUrgentOrders_ExecutesWithoutError() {
            List<OrderReadDto> result = orderMapper.findUrgentOrders();

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("반환된 긴급 주문은 COMPLETED 상태가 아니다")
        void findUrgentOrders_ExcludesCompletedOrders() {
            List<OrderReadDto> result = orderMapper.findUrgentOrders();
            assumeTrue(!result.isEmpty(), "긴급 주문 데이터 없음 — skip");

            result.forEach(dto ->
                    assertTrue(OrderStatus.COMPLETED != dto.getStatus(),
                            "긴급 주문 목록에 COMPLETED 상태가 포함되면 안 된다"));
        }
    }

    // ===== findOrderById =====

    @Nested
    @DisplayName("findOrderById — 주문 단건 상세 조회")
    class FindOrderById {

        @Test
        @DisplayName("존재하는 주문 ID로 조회하면 상세 정보가 반환된다")
        void findOrderById_ReturnsDetail_WhenExists() {
            assumeTrue(validOrderId != null, "주문 데이터 없음 — skip");

            OrderDetailDto result = orderMapper.findOrderById(validOrderId);

            assertNotNull(result, "상세 DTO 가 null 이면 안 된다");
            assertNotNull(result.getOrderId(),     "orderId 매핑 확인");
            assertNotNull(result.getOrderNumber(), "orderNumber 매핑 확인");
            assertNotNull(result.getStatus(),      "status 매핑 확인");
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID로 조회하면 null을 반환한다")
        void findOrderById_ReturnsNull_WhenNotExists() {
            OrderDetailDto result = orderMapper.findOrderById(-1L);

            assertTrue(result == null, "없는 주문 ID 조회 결과는 null 이어야 한다");
        }
    }

    // ===== findOrderSummary =====

    @Nested
    @DisplayName("findOrderSummary — 주문 현황 요약 집계 조회")
    class FindOrderSummary {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료되고 DTO가 반환된다")
        void findOrderSummary_ReturnsDto() {
            OrderSummaryDto result = orderMapper.findOrderSummary();

            assertNotNull(result, "요약 DTO 가 null 이면 안 된다");
        }

        @Test
        @DisplayName("집계 값들이 음수가 아니다")
        void findOrderSummary_CountsAreNonNegative() {
            OrderSummaryDto result = orderMapper.findOrderSummary();
            assumeTrue(result.getTotalCount() != null, "집계 데이터 없음 — skip");

            assertTrue(result.getTotalCount()        >= 0, "totalCount 음수 불가");
            assertTrue(result.getInProgressCount()   >= 0, "inProgressCount 음수 불가");
            assertTrue(result.getDeadlineRiskCount() >= 0, "deadlineRiskCount 음수 불가");
        }
    }

    // ===== findOrderOcsa =====

    @Nested
    @DisplayName("findOrderOcsa — 주문 OCSA 난이도 분석 결과 조회")
    class FindOrderOcsa {

        @Test
        @DisplayName("존재하는 주문 ID로 조회하면 OCSA 분석 결과가 반환된다")
        void findOrderOcsa_ReturnsDto_WhenExists() {
            assumeTrue(validOrderId != null, "주문 데이터 없음 — skip");

            OrderOcsaDto result = orderMapper.findOrderOcsa(validOrderId);

            assertNotNull(result, "OCSA DTO 가 null 이면 안 된다");
            assertNotNull(result.getOrderId(), "orderId 매핑 확인");
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID로 조회하면 null을 반환한다")
        void findOrderOcsa_ReturnsNull_WhenNotExists() {
            OrderOcsaDto result = orderMapper.findOrderOcsa(-1L);

            assertTrue(result == null, "없는 주문 ID 조회 결과는 null 이어야 한다");
        }
    }

    // ===== findUnassignedOrders =====

    @Nested
    @DisplayName("findUnassignedOrders — 미배정 주문 목록 조회 (ANALYZED 상태)")
    class FindUnassignedOrders {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
        void findUnassignedOrders_ExecutesWithoutError() {
            List<OrderReadDto> result = orderMapper.findUnassignedOrders();

            assertNotNull(result, "결과 리스트가 null 이면 안 된다");
        }

        @Test
        @DisplayName("반환된 주문은 모두 ANALYZED 상태다")
        void findUnassignedOrders_ReturnsOnlyAnalyzed() {
            List<OrderReadDto> result = orderMapper.findUnassignedOrders();
            assumeTrue(!result.isEmpty(), "ANALYZED 상태 주문 데이터 없음 — skip");

            result.forEach(dto ->
                    assertTrue(OrderStatus.ANALYZED == dto.getStatus(),
                            "미배정 목록에 ANALYZED 외 상태가 포함되면 안 된다"));
        }
    }

    // ===== findOcsaSummary =====

    @Nested
    @DisplayName("findOcsaSummary — OCSA 분석 현황 요약 집계 조회")
    class FindOcsaSummary {

        @Test
        @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료되고 DTO가 반환된다")
        void findOcsaSummary_ReturnsDto() {
            OcsaSummaryDto result = orderMapper.findOcsaSummary();

            assertNotNull(result, "OCSA 요약 DTO가 null 이면 안 된다");
        }

        @Test
        @DisplayName("집계 값이 존재하면 analyzedOrderCount 가 0 이상이다")
        void findOcsaSummary_CountIsNonNegative() {
            OcsaSummaryDto result = orderMapper.findOcsaSummary();
            assumeTrue(result.getAnalyzedOrderCount() != null, "집계 데이터 없음 — skip");

            assertTrue(result.getAnalyzedOrderCount() >= 0,
                    "analyzedOrderCount 는 음수가 될 수 없다");
        }
    }
}
