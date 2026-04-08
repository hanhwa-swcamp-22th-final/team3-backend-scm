package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.scm.query.dto.request.OrderQueryRequest;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OcsaSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderOcsaDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderReadDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 팀 리더(TL) 권한의 주문 조회 Query 서비스.
 * OrderMapper 를 통해 MyBatis 쿼리를 실행하고 결과를 컨트롤러에 전달한다.
 * 데이터 변경 없이 읽기 전용으로 동작한다.
 */
@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderMapper orderMapper;

    /**
     * 검색 조건에 따라 주문 목록을 조회한다.
     *
     * @param request 상태·난이도 등급 필터, 키워드, 페이징 조건을 담은 요청 DTO
     * @return 조건에 맞는 주문 요약 목록
     */
    public List<OrderReadDto> getOrders(OrderQueryRequest request) {
        return orderMapper.findOrders(request);
    }

    /**
     * 납기 3일 이내의 긴급 주문 목록을 조회한다.
     *
     * @return 긴급 주문 목록
     */
    public List<OrderReadDto> getUrgentOrders() {
        return orderMapper.findUrgentOrders();
    }

    /**
     * OCSA 분석 완료 후 기술자가 배정되지 않은 주문(ANALYZED 상태) 목록을 조회한다.
     *
     * @return 미배정 주문 목록
     */
    public List<OrderReadDto> getUnassignedOrders() {
        return orderMapper.findUnassignedOrders();
    }

    /**
     * 주문 ID로 주문 상세 정보를 조회한다.
     *
     * @param orderId 조회할 주문 PK
     * @return 주문 상세 DTO
     */
    public OrderDetailDto getOrderById(Long orderId) {
        return orderMapper.findOrderById(orderId);
    }

    /**
     * 전체 주문 현황 집계 요약을 조회한다.
     *
     * @return 주문 현황 요약 DTO
     */
    public OrderSummaryDto getOrderSummary() {
        return orderMapper.findOrderSummary();
    }

    /**
     * 특정 주문의 OCSA 난이도 분석 결과를 조회한다.
     *
     * @param orderId 조회할 주문 PK
     * @return OCSA 분석 결과 DTO
     */
    public OrderOcsaDto getOrderOcsa(Long orderId) {
        return orderMapper.findOrderOcsa(orderId);
    }

    /**
     * OCSA 분석 현황 요약(분석 주문 수, 평균 난이도 점수, 최고 난이도 등급)을 조회한다.
     *
     * @return OCSA 요약 DTO
     */
    public OcsaSummaryDto getOcsaSummary() {
        return orderMapper.findOcsaSummary();
    }
}
