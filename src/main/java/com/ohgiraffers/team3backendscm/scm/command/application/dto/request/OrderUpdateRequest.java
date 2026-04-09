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

@Getter
@NoArgsConstructor
public class OrderUpdateRequest {

    @NotNull(message = "Product id is required.")
    private Long productId;

    @NotBlank(message = "Order number is required.")
    private String orderNumber;

    @NotNull(message = "Order quantity is required.")
    @Min(value = 1, message = "Order quantity must be at least 1.")
    private Integer orderQuantity;

    @NotNull(message = "Due date is required.")
    private LocalDate dueDate;

    @NotNull(message = "Process step count is required.")
    @Min(value = 1, message = "Process step count must be at least 1.")
    @Max(value = 50, message = "Process step count must be 50 or less.")
    private Integer processStepCount;

    @NotNull(message = "Tolerance is required.")
    @DecimalMin(value = "0.0001", message = "Tolerance must be greater than 0.")
    @Digits(integer = 4, fraction = 4, message = "Tolerance supports up to 4 integer digits and 4 decimal digits.")
    private BigDecimal toleranceMm;

    @NotNull(message = "Skill level is required.")
    @Min(value = 1, message = "Skill level must be at least 1.")
    @Max(value = 5, message = "Skill level must be 5 or less.")
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
