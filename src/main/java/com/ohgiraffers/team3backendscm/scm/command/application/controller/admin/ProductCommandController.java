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
 * Admin 紐⑤뱢???몄텧?섎뒗 ?쒗뭹(Product) ?깅줉쨌?섏젙쨌??젣 REST 而⑦듃濡ㅻ윭.
 * 湲곕낯 寃쎈줈: /api/v1/scm/admin/products
 */
@RestController
@RequestMapping("/api/v1/scm/admin/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductCommandService productCommandService;

    /**
     * ?쒗뭹???깅줉?쒕떎.
     *
     * @param request ?쒗뭹紐끒룹퐫?쒕? ?댁? ?붿껌 DTO
     * @return ?앹꽦???쒗뭹 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        Long productId = productCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(productId));
    }

    /**
     * ?쒗뭹 ?뺣낫瑜??섏젙?쒕떎.
     *
     * @param productId ?섏젙???쒗뭹 ID
     * @param request   蹂寃쏀븷 ?쒗뭹紐끒룹퐫?쒕? ?댁? ?붿껌 DTO
     * @return ?깃났 ?묐떟 (data = null)
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        productCommandService.update(productId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * ?쒗뭹????젣?쒕떎.
     *
     * @param productId ??젣???쒗뭹 ID
     * @return ?깃났 ?묐떟 (data = null)
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
        productCommandService.delete(productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
