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
 * Admin 紐⑤뱢???몄텧?섎뒗 二쇰Ц(Order) ?깅줉쨌?섏젙쨌??젣 REST 而⑦듃濡ㅻ윭.
 * 湲곕낯 寃쎈줈: /api/v1/scm/admin/orders
 * <p>
 * SCM ?뚰겕?뚮줈??議고쉶쨌?곹깭 蹂寃???蹂꾨룄 TL/Worker 而⑦듃濡ㅻ윭?먯꽌 泥섎━?쒕떎.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm/admin/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderCommandService orderCommandService;

    /**
     * 二쇰Ц???깅줉?쒕떎. 珥덇린 ?곹깭??REGISTERED濡?怨좎젙?쒕떎.
     *
     * @param request 二쇰Ц ?뺣낫瑜??댁? ?붿껌 DTO
     * @return ?앹꽦??二쇰Ц ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Long orderId = orderCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(orderId));
    }

    /**
     * 二쇰Ц 湲곕낯 ?뺣낫瑜??섏젙?쒕떎. REGISTERED ?곹깭??二쇰Ц留??덉슜?쒕떎.
     *
     * @param orderId ?섏젙??二쇰Ц ID
     * @param request 蹂寃쏀븷 ?뺣낫瑜??댁? ?붿껌 DTO
     * @return ?깃났 ?묐떟 (data = null)
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> updateOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpdateRequest request) {
        orderCommandService.update(orderId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 二쇰Ц????젣?쒕떎. REGISTERED ?곹깭??二쇰Ц留??덉슜?쒕떎.
     *
     * @param orderId ??젣??二쇰Ц ID
     * @return ?깃났 ?묐떟 (data = null)
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        orderCommandService.delete(orderId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
