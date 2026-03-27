package com.ohgiraffers.team3backendscm.scm.query.dto;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderReadDto {

    private String orderNumber;
    private String itemName;
    private OrderStatus status;
    private LocalDate dueDate;
    private Long technicianId;

    /** 테스트용 간략 생성자 */
    public OrderReadDto(String orderNumber, String itemName, OrderStatus status) {
        this.orderNumber = orderNumber;
        this.itemName = itemName;
        this.status = status;
    }
}
