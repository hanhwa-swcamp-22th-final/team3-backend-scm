package com.ohgiraffers.team3backendscm.scm.command.application.service.admin;

import com.ohgiraffers.team3backendscm.common.exception.ErrorCode;
import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Product;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Admin???¸ì¶œ?˜ëŠ” ?œí’ˆ(Product) ?±ë¡Â·?˜ì •Â·?? œ Command ?œë¹„??
 */
@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final IdGenerator idGenerator;

    /**
     * ?œí’ˆ???±ë¡?œë‹¤.
     *
     * @param request ?œí’ˆëª…Â·ì½”?œë? ?´ì? ?”ì²­ DTO
     * @return ?ì„±???œí’ˆ ID
     */
    @Transactional
    public Long create(ProductCreateRequest request) {
        Long id = idGenerator.generate();
        Product product = Product.create(id, request.getProductName(), request.getProductCode());
        productRepository.save(product);
        return id;
    }

    /**
     * ?œí’ˆ ?•ë³´ë¥??˜ì •?œë‹¤.
     *
     * @param productId ?˜ì •???œí’ˆ ID
     * @param request   ë³€ê²½í•  ?œí’ˆëª…Â·ì½”?œë? ?´ì? ?”ì²­ DTO
     * @throws NoSuchElementException ?œí’ˆ??ì°¾ì„ ???†ì„ ê²½ìš°
     */
    @Transactional
    public void update(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
        product.update(request.getProductName(), request.getProductCode());
        productRepository.save(product);
    }

    /**
     * ?œí’ˆ???? œ?œë‹¤.
     *
     * @param productId ?? œ???œí’ˆ ID
     * @throws NoSuchElementException ?œí’ˆ??ì°¾ì„ ???†ì„ ê²½ìš°
     */
    @Transactional
    public void delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
        productRepository.delete(product);
    }
}
