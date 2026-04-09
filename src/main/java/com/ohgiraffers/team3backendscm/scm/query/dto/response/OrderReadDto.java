package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 주문 목록 조회 시 반환되는 응답 DTO.
 * 주문 번호, 상품명, 상태, 납기일, 배정 기술자 ID 등 목록 화면에서 필요한 요약 정보를 담는다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderReadDto {

    private String orderNumber;    // 주문 번호
    private String itemName;       // 상품(품목) 명칭
    private OrderStatus status;    // 주문 처리 상태
    private LocalDate dueDate;     // 납기 마감일
    private Long technicianId;     // 배정된 기술자 ID (미배정 시 null)

    /**
     * 테스트용 간략 생성자. 납기일·기술자 ID 없이 상태만 지정한다.
     *
     * @param orderNumber 주문 번호
     * @param itemName    상품명
     * @param status      주문 상태
     */

    public OrderReadDto(String orderNumber, String itemName, OrderStatus status) {
        this.orderNumber = orderNumber;
        this.itemName = itemName;
        this.status = status;
    }
}
