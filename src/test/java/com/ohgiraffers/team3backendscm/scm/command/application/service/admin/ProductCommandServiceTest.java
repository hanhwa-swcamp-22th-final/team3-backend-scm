package com.ohgiraffers.team3backendscm.scm.command.application.service.admin;

import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Product;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductCommandServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ProductCommandService productCommandService;

    @Test
    @DisplayName("제품 등록 성공: 새로운 제품이 생성되고 repository.save()가 호출된다")
    void createProduct_Success() {
        // given
        ProductCreateRequest request = new ProductCreateRequest("테스트 제품", "TEST-CODE");
        given(idGenerator.generate()).willReturn(10L);

        // when
        Long id = productCommandService.create(request);

        // then
        assertEquals(10L, id);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("제품 수정 성공: 제품명과 코드를 수정한다")
    void updateProduct_Success() {
        // given
        Product product = Product.create(1L, "기존 이름", "OLD-CODE");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ProductUpdateRequest request = new ProductUpdateRequest("새 이름", "NEW-CODE");

        // when
        productCommandService.update(1L, request);

        // then
        verify(productRepository, times(1)).save(product);
        assertEquals("새 이름", product.getProductName());
        assertEquals("NEW-CODE", product.getProductCode());
    }

    @Test
    @DisplayName("제품 삭제 성공: 제품을 삭제한다")
    void deleteProduct_Success() {
        // given
        Product product = Product.create(1L, "기존 이름", "OLD-CODE");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when
        productCommandService.delete(1L);

        // then
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("제품 수정 실패: 존재하지 않는 제품 ID로 수정 요청 시 NoSuchElementException 발생")
    void updateProduct_Fail_WhenNotFound() {
        // given
        given(productRepository.findById(99L)).willReturn(Optional.empty());
        ProductUpdateRequest request = new ProductUpdateRequest("새 이름", "NEW-CODE");

        // when & then
        assertThrows(NoSuchElementException.class,
                () -> productCommandService.update(99L, request));
    }

    @Test
    @DisplayName("제품 삭제 실패: 존재하지 않는 제품 ID로 삭제 요청 시 NoSuchElementException 발생")
    void deleteProduct_Fail_WhenNotFound() {
        // given
        given(productRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class,
                () -> productCommandService.delete(99L));
    }
}
