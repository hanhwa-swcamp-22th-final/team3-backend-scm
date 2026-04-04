package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Admin이 SCM의 주문 정보를 수정할 때 사용하는 요청 DTO.
 * REGISTERED 상태인 주문만 수정 가능하다.
 */
@Getter
@NoArgsConstructor
public class OrderUpdateRequest {

    @NotNull(message = "제품 ID는 필수입니다.")
    private Long productId; // 변경할 제품 ID

    @NotBlank(message = "주문 번호는 필수입니다.")
    private String orderNumber; // 변경할 주문 번호

    @NotNull(message = "주문 수량은 필수입니다.")
    @Min(value = 1, message = "주문 수량은 1 이상이어야 합니다.")
    private Integer orderQuantity; // 변경할 주문 수량

    @NotNull(message = "납기 마감일은 필수입니다.")
    private LocalDate dueDate; // 변경할 납기 마감일

    public OrderUpdateRequest(Long productId, String orderNumber, Integer orderQuantity, LocalDate dueDate) {
        this.productId = productId;
        this.orderNumber = orderNumber;
        this.orderQuantity = orderQuantity;
        this.dueDate = dueDate;
    }
}
