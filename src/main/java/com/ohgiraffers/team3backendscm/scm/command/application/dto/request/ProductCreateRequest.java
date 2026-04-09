package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Admin??SCM???쒗뭹???깅줉?????ъ슜?섎뒗 ?붿껌 DTO.
 */
@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "?쒗뭹紐낆? ?꾩닔?낅땲??")
    private String productName; // ?쒗뭹 紐낆묶

    @NotBlank(message = "?쒗뭹 肄붾뱶???꾩닔?낅땲??")
    private String productCode; // ?쒗뭹 怨좎쑀 肄붾뱶

    public ProductCreateRequest(String productName, String productCode) {
        this.productName = productName;
        this.productCode = productCode;
    }
}
