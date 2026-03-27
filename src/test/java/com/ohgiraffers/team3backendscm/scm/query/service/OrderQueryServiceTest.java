package com.ohgiraffers.team3backendscm.scm.query.service;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.query.dto.OrderQueryRequest;
import com.ohgiraffers.team3backendscm.scm.query.dto.OrderReadDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.OrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
}
