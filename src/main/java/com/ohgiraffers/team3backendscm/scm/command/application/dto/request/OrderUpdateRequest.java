package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Admin??SCM??ì£¼ë¬¸ ?•ë³´ë¥??˜ì •?????¬ìš©?˜ëŠ” ?”ì²­ DTO.
 * REGISTERED ?íƒœ??ì£¼ë¬¸ë§??˜ì • ê°€?¥í•˜??
 */
@Getter
@NoArgsConstructor
public class OrderUpdateRequest {

    @NotNull(message = "?œí’ˆ ID???„ìˆ˜?…ë‹ˆ??")
    private Long productId; // ë³€ê²½í•  ?œí’ˆ ID

    @NotBlank(message = "ì£¼ë¬¸ ë²ˆí˜¸???„ìˆ˜?…ë‹ˆ??")
    private String orderNumber; // ë³€ê²½í•  ì£¼ë¬¸ ë²ˆí˜¸

    @NotNull(message = "ì£¼ë¬¸ ?˜ëŸ‰?€ ?„ìˆ˜?…ë‹ˆ??")
    @Min(value = 1, message = "ì£¼ë¬¸ ?˜ëŸ‰?€ 1 ?´ìƒ?´ì–´???©ë‹ˆ??")
    private Integer orderQuantity; // ë³€ê²½í•  ì£¼ë¬¸ ?˜ëŸ‰

    @NotNull(message = "?©ê¸° ë§ˆê°?¼ì? ?„ìˆ˜?…ë‹ˆ??")
    private LocalDate dueDate; // ë³€ê²½í•  ?©ê¸° ë§ˆê°??

    public OrderUpdateRequest(Long productId, String orderNumber, Integer orderQuantity, LocalDate dueDate) {
        this.productId = productId;
        this.orderNumber = orderNumber;
        this.orderQuantity = orderQuantity;
        this.dueDate = dueDate;
    }
}
