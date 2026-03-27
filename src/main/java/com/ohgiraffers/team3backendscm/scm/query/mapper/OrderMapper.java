package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.OrderQueryRequest;
import com.ohgiraffers.team3backendscm.scm.query.dto.OrderReadDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {

    List<OrderReadDto> findOrders(OrderQueryRequest request);

    List<OrderReadDto> findUrgentOrders();
}
