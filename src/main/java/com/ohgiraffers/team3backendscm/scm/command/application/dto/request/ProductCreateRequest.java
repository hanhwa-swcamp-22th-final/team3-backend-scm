package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Admin SCM 에서 상품 등록 시 사용하는 요청 DTO이다.
 */
@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    private String productName;

    @NotBlank(message = "상품 코드는 필수입니다.")
    private String productCode;

    public ProductCreateRequest(String productName, String productCode) {
        this.productName = productName;
        this.productCode = productCode;
    }
}