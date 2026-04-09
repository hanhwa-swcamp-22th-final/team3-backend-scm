package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Admin??SCM???œí’ˆ ?•ë³´ë¥??˜ì •?????¬ìš©?˜ëŠ” ?”ì²­ DTO.
 */
@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    @NotBlank(message = "?œí’ˆëª…ì? ?„ìˆ˜?…ë‹ˆ??")
    private String productName; // ë³€ê²½í•  ?œí’ˆ ëª…ì¹­

    @NotBlank(message = "?œí’ˆ ì½”ë“œ???„ìˆ˜?…ë‹ˆ??")
    private String productCode; // ë³€ê²½í•  ?œí’ˆ ê³ ìœ  ì½”ë“œ

    public ProductUpdateRequest(String productName, String productCode) {
        this.productName = productName;
        this.productCode = productCode;
    }
}
