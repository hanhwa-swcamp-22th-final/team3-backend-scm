package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Admin이 SCM의 제품 정보를 수정할 때 사용하는 요청 DTO.
 */
@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    @NotBlank(message = "제품명은 필수입니다.")
    private String productName; // 변경할 제품 명칭

    @NotBlank(message = "제품 코드는 필수입니다.")
    private String productCode; // 변경할 제품 고유 코드

    public ProductUpdateRequest(String productName, String productCode) {
        this.productName = productName;
        this.productCode = productCode;
    }
}
