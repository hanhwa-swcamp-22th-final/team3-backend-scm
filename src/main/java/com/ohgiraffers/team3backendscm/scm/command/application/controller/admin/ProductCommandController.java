package com.ohgiraffers.team3backendscm.scm.command.application.controller.admin;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.admin.ProductCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 모듈에서 호출하는 상품 등록, 수정, 삭제용 REST 컨트롤러이다.
 * 기본 경로는 /api/v1/scm/admin/products 이다.
 */
@RestController
@RequestMapping("/api/v1/scm/admin/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductCommandService productCommandService;

    /**
     * 상품을 등록한다.
     *
     * @param request 상품 등록 요청 DTO
     * @return 생성된 상품 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        Long productId = productCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(productId));
    }

    /**
     * 상품 정보를 수정한다.
     *
     * @param productId 수정할 상품 ID
     * @param request 상품 수정 요청 DTO
     * @return 성공 응답
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        productCommandService.update(productId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 상품을 삭제한다.
     *
     * @param productId 삭제할 상품 ID
     * @return 성공 응답
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
        productCommandService.delete(productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}