package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @NotNull(message = "공정 단계 수는 필수입니다.")
    @Min(value = 1, message = "공정 단계 수는 1 이상이어야 합니다.")
    @Max(value = 50, message = "공정 단계 수는 50 이하여야 합니다.")
    private Integer processStepCount;

    @NotNull(message = "허용 공차는 필수입니다.")
    @DecimalMin(value = "0.0001", message = "허용 공차는 0보다 커야 합니다.")
    @Digits(integer = 4, fraction = 4, message = "허용 공차는 소수점 넷째 자리까지 입력 가능합니다.")
    private BigDecimal toleranceMm;

    @NotNull(message = "스킬 레벨은 필수입니다.")
    @Min(value = 1, message = "스킬 레벨은 1 이상이어야 합니다.")
    @Max(value = 5, message = "스킬 레벨은 5 이하여야 합니다.")
    private Integer skillLevel;

    public OrderUpdateRequest(Long productId, String orderNumber, Integer orderQuantity, LocalDate dueDate) {
        this(productId, orderNumber, orderQuantity, dueDate, 1, new BigDecimal("0.1000"), 1);
    }

    public OrderUpdateRequest(
        Long productId,
        String orderNumber,
        Integer orderQuantity,
        LocalDate dueDate,
        Integer processStepCount,
        BigDecimal toleranceMm,
        Integer skillLevel
    ) {
        this.productId = productId;
        this.orderNumber = orderNumber;
        this.orderQuantity = orderQuantity;
        this.dueDate = dueDate;
        this.processStepCount = processStepCount;
        this.toleranceMm = toleranceMm;
        this.skillLevel = skillLevel;
    }
}
