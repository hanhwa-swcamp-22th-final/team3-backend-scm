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
 * Admin이 호출하는 제품(Product) 등록·수정·삭제 Command 서비스.
 */
@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final IdGenerator idGenerator;

    /**
     * 제품을 등록한다.
     *
     * @param request 제품명·코드를 담은 요청 DTO
     * @return 생성된 제품 ID
     */
    @Transactional
    public Long create(ProductCreateRequest request) {
        Long id = idGenerator.generate();
        Product product = Product.create(id, request.getProductName(), request.getProductCode());
        productRepository.save(product);
        return id;
    }

    /**
     * 제품 정보를 수정한다.
     *
     * @param productId 수정할 제품 ID
     * @param request   변경할 제품명·코드를 담은 요청 DTO
     * @throws NoSuchElementException 제품을 찾을 수 없을 경우
     */
    @Transactional
    public void update(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
        product.update(request.getProductName(), request.getProductCode());
        productRepository.save(product);
    }

    /**
     * 제품을 삭제한다.
     *
     * @param productId 삭제할 제품 ID
     * @throws NoSuchElementException 제품을 찾을 수 없을 경우
     */
    @Transactional
    public void delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
        productRepository.delete(product);
    }
}
