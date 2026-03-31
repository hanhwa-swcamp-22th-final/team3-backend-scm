package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.DifficultyGrade;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 주문 상세 조회 시 반환되는 응답 DTO.
 * 목록 DTO(OrderReadDto) 대비 주문 수량, OCSA 난이도 점수·등급 등
 * 상세 화면에서 필요한 추가 정보를 포함한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {

    private Long orderId;              // 주문 PK
    private String orderNumber;        // 주문 번호
    private String itemName;           // 제품(품목) 명칭
    private OrderStatus status;        // 주문 처리 상태
    private LocalDate dueDate;         // 납기 마감일
    private Integer orderQuantity;     // 주문 수량
    private Long technicianId;         // 배정된 기술자 ID (미배정 시 null)
    private BigDecimal difficultyScore; // OCSA 산출 난이도 점수
    private DifficultyGrade difficultyGrade; // 난이도 등급 (D1~D5)
}
