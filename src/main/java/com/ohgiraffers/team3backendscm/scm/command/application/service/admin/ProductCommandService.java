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
 * Admin???몄텧?섎뒗 ?쒗뭹(Product) ?깅줉쨌?섏젙쨌??젣 Command ?쒕퉬??
 */
@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final IdGenerator idGenerator;

    /**
     * ?쒗뭹???깅줉?쒕떎.
     *
     * @param request ?쒗뭹紐끒룹퐫?쒕? ?댁? ?붿껌 DTO
     * @return ?앹꽦???쒗뭹 ID
     */
    @Transactional
    public Long create(ProductCreateRequest request) {
        Long id = idGenerator.generate();
        Product product = Product.create(id, request.getProductName(), request.getProductCode());
        productRepository.save(product);
        return id;
    }

    /**
     * ?쒗뭹 ?뺣낫瑜??섏젙?쒕떎.
     *
     * @param productId ?섏젙???쒗뭹 ID
     * @param request   蹂寃쏀븷 ?쒗뭹紐끒룹퐫?쒕? ?댁? ?붿껌 DTO
     * @throws NoSuchElementException ?쒗뭹??李얠쓣 ???놁쓣 寃쎌슦
     */
    @Transactional
    public void update(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
        product.update(request.getProductName(), request.getProductCode());
        productRepository.save(product);
    }

    /**
     * ?쒗뭹????젣?쒕떎.
     *
     * @param productId ??젣???쒗뭹 ID
     * @throws NoSuchElementException ?쒗뭹??李얠쓣 ???놁쓣 寃쎌슦
     */
    @Transactional
    public void delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
        productRepository.delete(product);
    }
}
