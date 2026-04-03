package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.query.dto.request.OrderQueryRequest;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OcsaSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderOcsaDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderReadDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.OrderMapper;
import com.ohgiraffers.team3backendscm.scm.query.service.tl.OrderQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderQueryService orderQueryService;

    @Test
    @DisplayName("주문 목록 조회 시 Mapper가 1회 호출된다")
    void getOrders_CallsMapperOnce() {
        // given
        given(orderMapper.findOrders(any())).willReturn(List.of());

        // when
        orderQueryService.getOrders(new OrderQueryRequest());

        // then
        verify(orderMapper, times(1)).findOrders(any());
    }

    @Test
    @DisplayName("긴급 주문 조회 시 긴급 주문만 반환된다")
    void getUrgentOrders_ReturnsOnlyUrgent() {
        // given
        List<OrderReadDto> mockOrders = List.of(
                new OrderReadDto("ORD-0301", "D-2 긴급", OrderStatus.INPROGRESS)
        );
        given(orderMapper.findUrgentOrders()).willReturn(mockOrders);

        // when
        List<OrderReadDto> result = orderQueryService.getUrgentOrders();

        // then
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("주문 상세 조회 시 Mapper가 1회 호출된다")
    void getOrderById_CallsMapperOnce() {
        // given
        given(orderMapper.findOrderById(anyLong())).willReturn(new OrderDetailDto());

        // when
        orderQueryService.getOrderById(1L);

        // then
        verify(orderMapper, times(1)).findOrderById(anyLong());
    }

    @Test
    @DisplayName("주문 요약 조회 시 Mapper가 1회 호출되고 결과를 반환한다")
    void getOrderSummary_ReturnsSummary() {
        // given
        OrderSummaryDto mockSummary = new OrderSummaryDto(10, 5, 2, 80.0, 3, 4, 3);
        given(orderMapper.findOrderSummary()).willReturn(mockSummary);

        // when
        OrderSummaryDto result = orderQueryService.getOrderSummary();

        // then
        verify(orderMapper, times(1)).findOrderSummary();
        assertEquals(10, result.getTotalCount());
        assertEquals(80.0, result.getAchievementRate());
    }

    @Test
    @DisplayName("OCSA 분석 조회 시 Mapper가 1회 호출된다")
    void getOrderOcsa_CallsMapperOnce() {
        // given
        given(orderMapper.findOrderOcsa(anyLong())).willReturn(new OrderOcsaDto());

        // when
        orderQueryService.getOrderOcsa(1L);

        // then
        verify(orderMapper, times(1)).findOrderOcsa(anyLong());
    }

    @Test
    @DisplayName("미배정 주문 조회 시 Mapper가 1회 호출된다")
    void getUnassignedOrders_CallsMapperOnce() {
        // given
        given(orderMapper.findUnassignedOrders()).willReturn(List.of());

        // when
        orderQueryService.getUnassignedOrders();

        // then
        verify(orderMapper, times(1)).findUnassignedOrders();
    }

    @Test
    @DisplayName("OCSA 요약 조회 시 Mapper가 1회 호출되고 결과를 반환한다")
    void getOcsaSummary_ReturnsSummary() {
        // given
        OcsaSummaryDto mockSummary = new OcsaSummaryDto(5, 3.2, "D4");
        given(orderMapper.findOcsaSummary()).willReturn(mockSummary);

        // when
        OcsaSummaryDto result = orderQueryService.getOcsaSummary();

        // then
        verify(orderMapper, times(1)).findOcsaSummary();
        assertEquals(5, result.getAnalyzedOrderCount());
        assertEquals("D4", result.getMaxDifficultyGrade());
    }
}
