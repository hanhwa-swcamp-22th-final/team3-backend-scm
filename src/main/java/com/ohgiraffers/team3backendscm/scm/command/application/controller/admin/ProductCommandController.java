package com.ohgiraffers.team3backendscm.scm.command.application.controller.admin;

import com.ohgiraffers.team3backendscm.common.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.admin.ProductCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 모듈이 호출하는 제품(Product) 등록·수정·삭제 REST 컨트롤러.
 * 기본 경로: /api/v1/scm/admin/products
 */
@RestController
@RequestMapping("/api/v1/scm/admin/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductCommandService productCommandService;

    /**
     * 제품을 등록한다.
     *
     * @param request 제품명·코드를 담은 요청 DTO
     * @return 생성된 제품 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        Long productId = productCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(productId));
    }

    /**
     * 제품 정보를 수정한다.
     *
     * @param productId 수정할 제품 ID
     * @param request   변경할 제품명·코드를 담은 요청 DTO
     * @return 성공 응답 (data = null)
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        productCommandService.update(productId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 제품을 삭제한다.
     *
     * @param productId 삭제할 제품 ID
     * @return 성공 응답 (data = null)
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
        productCommandService.delete(productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
