package com.ohgiraffers.team3backendscm.scm.query.dto.request;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.DifficultyGrade;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문 목록 조회 시 사용하는 검색 조건 요청 DTO.
 * GET /api/v1/scm/orders 엔드포인트의 쿼리 파라미터를 바인딩한다.
 * MyBatis OrderMapper 의 findOrders 메서드에 그대로 전달된다.
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderQueryRequest {

    private OrderStatus status;          // 조회할 주문 상태 필터 (null 이면 전체)
    private DifficultyGrade difficultyGrade; // 조회할 난이도 등급 필터 (null 이면 전체)
    private String keyword;              // 주문 번호 또는 제품명 키워드 검색
    private Integer page;                // 페이지 번호 (1-based)
    private Integer size;                // 페이지당 항목 수
}
