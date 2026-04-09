package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Admin??SCM???쒗뭹 ?뺣낫瑜??섏젙?????ъ슜?섎뒗 ?붿껌 DTO.
 */
@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    @NotBlank(message = "?쒗뭹紐낆? ?꾩닔?낅땲??")
    private String productName; // 蹂寃쏀븷 ?쒗뭹 紐낆묶

    @NotBlank(message = "?쒗뭹 肄붾뱶???꾩닔?낅땲??")
    private String productCode; // 蹂寃쏀븷 ?쒗뭹 怨좎쑀 肄붾뱶

    public ProductUpdateRequest(String productName, String productCode) {
        this.productName = productName;
        this.productCode = productCode;
    }
}
