package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Admin이 SCM에 주문을 등록할 때 사용하는 요청 DTO.
 * 등록된 주문은 REGISTERED 상태로 시작하며, OCSA 분석 후 SCM 워크플로우가 진행된다.
 */
@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "제품 ID는 필수입니다.")
    private Long productId; // 주문 대상 제품 ID

    @NotNull(message = "OCSA 설정 ID는 필수입니다.")
    private Long configId; // OCSA 가중치 설정 ID

    @NotBlank(message = "주문 번호는 필수입니다.")
    private String orderNumber; // 사람이 읽을 수 있는 주문 번호 (예: ORD-20240101-001)

    @NotNull(message = "주문 수량은 필수입니다.")
    @Min(value = 1, message = "주문 수량은 1 이상이어야 합니다.")
    private Integer orderQuantity; // 주문 수량

    @NotNull(message = "납기 마감일은 필수입니다.")
    private LocalDate dueDate; // 납기 마감일

    private Boolean isFirstOrder = false; // 해당 제품의 최초 주문 여부 (기본값: false)

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

    public OrderCreateRequest(Long productId, Long configId, String orderNumber,
                              Integer orderQuantity, LocalDate dueDate, Boolean isFirstOrder) {
        this(productId, configId, orderNumber, orderQuantity, dueDate, 1, new BigDecimal("0.1000"), 1, isFirstOrder);
    }

    public OrderCreateRequest(Long productId, Long configId, String orderNumber,
                              Integer orderQuantity, LocalDate dueDate, Integer processStepCount,
                              BigDecimal toleranceMm, Integer skillLevel, Boolean isFirstOrder) {
        this.productId = productId;
        this.configId = configId;
        this.orderNumber = orderNumber;
        this.orderQuantity = orderQuantity;
        this.dueDate = dueDate;
        this.processStepCount = processStepCount;
        this.toleranceMm = toleranceMm;
        this.skillLevel = skillLevel;
        this.isFirstOrder = isFirstOrder;
    }
}
