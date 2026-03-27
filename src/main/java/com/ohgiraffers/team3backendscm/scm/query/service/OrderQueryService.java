package com.ohgiraffers.team3backendscm.scm.query.service;

import com.ohgiraffers.team3backendscm.scm.query.dto.OrderQueryRequest;
import com.ohgiraffers.team3backendscm.scm.query.dto.OrderReadDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderMapper orderMapper;

    public List<OrderReadDto> getOrders(OrderQueryRequest request) {
        return orderMapper.findOrders(request);
    }

    public List<OrderReadDto> getUrgentOrders() {
        return orderMapper.findUrgentOrders();
    }
}
