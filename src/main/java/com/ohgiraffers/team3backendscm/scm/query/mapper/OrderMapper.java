package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.request.OrderQueryRequest;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderOcsaDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderReadDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderSummaryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 주문(Order) 조회 전용 MyBatis 매퍼 인터페이스.
 * SQL은 src/main/resources/mappers/orders.xml 에 정의된다.
 * Query 레이어에서만 사용하며, 데이터 변경(INSERT/UPDATE/DELETE)은 수행하지 않는다.
 */
@Mapper
public interface OrderMapper {

    /**
     * 검색 조건(상태 필터, 키워드, 페이징)에 따라 주문 목록을 조회한다.
     *
     * @param request 검색 조건 DTO
     * @return 조건에 맞는 주문 요약 목록
     */
    List<OrderReadDto> findOrders(OrderQueryRequest request);

    /**
     * 납기 3일 이내의 긴급 주문 목록을 조회한다.
     *
     * @return 긴급 주문 목록
     */
    List<OrderReadDto> findUrgentOrders();

    /**
     * 주문 ID로 단건 상세 정보를 조회한다.
     *
     * @param orderId 조회할 주문 PK
     * @return 주문 상세 DTO (없으면 null)
     */
    OrderDetailDto findOrderById(Long orderId);

    /**
     * 전체 주문 현황 집계 요약(총 수, 진행 중, 납기 위험, 달성률)을 조회한다.
     *
     * @return 주문 요약 DTO
     */
    OrderSummaryDto findOrderSummary();

    /**
     * 특정 주문의 OCSA 난이도 분석 결과(V1~V4, α, 점수, 등급, 추천 기술자)를 조회한다.
     *
     * @param orderId 조회할 주문 PK
     * @return OCSA 분석 결과 DTO (없으면 null)
     */
    OrderOcsaDto findOrderOcsa(Long orderId);
}
