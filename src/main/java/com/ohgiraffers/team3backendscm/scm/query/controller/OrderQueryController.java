package com.ohgiraffers.team3backendscm.scm.query.controller;

import com.ohgiraffers.team3backendscm.common.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.OrderQueryRequest;
import com.ohgiraffers.team3backendscm.scm.query.dto.OrderReadDto;
import com.ohgiraffers.team3backendscm.scm.query.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderReadDto>>> getOrders(
            @ModelAttribute OrderQueryRequest request) {
        List<OrderReadDto> orders = orderQueryService.getOrders(request);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/orders/urgent")
    public ResponseEntity<ApiResponse<List<OrderReadDto>>> getUrgentOrders() {
        List<OrderReadDto> orders = orderQueryService.getUrgentOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}
