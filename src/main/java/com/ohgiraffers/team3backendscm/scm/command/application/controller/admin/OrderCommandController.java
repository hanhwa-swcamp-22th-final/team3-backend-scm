package com.ohgiraffers.team3backendscm.scm.command.application.controller.admin;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.admin.OrderCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin ëª¨ë“ˆ???¸ì¶œ?˜ëŠ” ì£¼ë¬¸(Order) ?±ë¡Â·?˜ì •Â·?? œ REST ì»¨íŠ¸ë¡¤ëŸ¬.
 * ê¸°ë³¸ ê²½ë¡œ: /api/v1/scm/admin/orders
 * <p>
 * SCM ?Œí¬?Œë¡œ??ì¡°íšŒÂ·?íƒœ ë³€ê²???ë³„ë„ TL/Worker ì»¨íŠ¸ë¡¤ëŸ¬?ì„œ ì²˜ë¦¬?œë‹¤.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm/admin/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderCommandService orderCommandService;

    /**
     * ì£¼ë¬¸???±ë¡?œë‹¤. ì´ˆê¸° ?íƒœ??REGISTEREDë¡?ê³ ì •?œë‹¤.
     *
     * @param request ì£¼ë¬¸ ?•ë³´ë¥??´ì? ?”ì²­ DTO
     * @return ?ì„±??ì£¼ë¬¸ ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Long orderId = orderCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(orderId));
    }

    /**
     * ì£¼ë¬¸ ê¸°ë³¸ ?•ë³´ë¥??˜ì •?œë‹¤. REGISTERED ?íƒœ??ì£¼ë¬¸ë§??ˆìš©?œë‹¤.
     *
     * @param orderId ?˜ì •??ì£¼ë¬¸ ID
     * @param request ë³€ê²½í•  ?•ë³´ë¥??´ì? ?”ì²­ DTO
     * @return ?±ê³µ ?‘ë‹µ (data = null)
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> updateOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpdateRequest request) {
        orderCommandService.update(orderId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * ì£¼ë¬¸???? œ?œë‹¤. REGISTERED ?íƒœ??ì£¼ë¬¸ë§??ˆìš©?œë‹¤.
     *
     * @param orderId ?? œ??ì£¼ë¬¸ ID
     * @return ?±ê³µ ?‘ë‹µ (data = null)
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        orderCommandService.delete(orderId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
