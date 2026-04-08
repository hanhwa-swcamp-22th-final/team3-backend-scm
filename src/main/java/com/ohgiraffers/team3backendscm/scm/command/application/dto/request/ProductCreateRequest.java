package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Admin이 SCM에 제품을 등록할 때 사용하는 요청 DTO.
 */
@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "제품명은 필수입니다.")
    private String productName; // 제품 명칭

    @NotBlank(message = "제품 코드는 필수입니다.")
    private String productCode; // 제품 고유 코드

    public ProductCreateRequest(String productName, String productCode) {
        this.productName = productName;
        this.productCode = productCode;
    }
}
