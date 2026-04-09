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
 * Admin ëª¨ë“ˆ???¸ì¶œ?˜ëŠ” ?œí’ˆ(Product) ?±ë¡Â·?˜ì •Â·?? œ REST ì»¨íŠ¸ë¡¤ëŸ¬.
 * ê¸°ë³¸ ê²½ë¡œ: /api/v1/scm/admin/products
 */
@RestController
@RequestMapping("/api/v1/scm/admin/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductCommandService productCommandService;

    /**
     * ?œí’ˆ???±ë¡?œë‹¤.
     *
     * @param request ?œí’ˆëª…Â·ì½”?œë? ?´ì? ?”ì²­ DTO
     * @return ?ì„±???œí’ˆ ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        Long productId = productCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(productId));
    }

    /**
     * ?œí’ˆ ?•ë³´ë¥??˜ì •?œë‹¤.
     *
     * @param productId ?˜ì •???œí’ˆ ID
     * @param request   ë³€ê²½í•  ?œí’ˆëª…Â·ì½”?œë? ?´ì? ?”ì²­ DTO
     * @return ?±ê³µ ?‘ë‹µ (data = null)
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        productCommandService.update(productId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * ?œí’ˆ???? œ?œë‹¤.
     *
     * @param productId ?? œ???œí’ˆ ID
     * @return ?±ê³µ ?‘ë‹µ (data = null)
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long productId) {
        productCommandService.delete(productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
