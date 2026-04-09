package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 주문 대상이 되는 상품(Product) 엔티티.
 * 상품 이름·코드를 관리하며 Order 엔티티에서 product_id 로 참조한다.
 */
@Entity
@Table(name = "product")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @Column(name = "product_id")
    private Long productId; // 상품 PK

    @Column(name = "product_name", nullable = false)
    private String productName; // 상품 명칭

    @Column(name = "product_code", nullable = false)
    private String productCode; // 상품 코드 (고유 식별 코드)

    // 이하 JPA Auditing으로 자동 채워지는 필드들

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 레코드 최초 생성 일시

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy; // 레코드 최초 생성자 (employee_id)

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 레코드 최종 수정 일시

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy; // 레코드 최종 수정자 (employee_id)

    /**
     * Admin 에서 상품을 등록할 때 사용하는 정적 팩토리 메서드.
     *
     * @param id          상품 PK (IdGenerator로 생성)
     * @param productName 상품 명칭
     * @param productCode 상품 고유 코드
     * @return 생성된 Product 인스턴스
     */
    public static Product create(Long id, String productName, String productCode) {
        Product product = new Product();
        product.productId = id;
        product.productName = productName;
        product.productCode = productCode;
        return product;
    }

    /**
     * 상품 정보를 수정한다. Admin 에서 상품명·코드 변경 시 호출된다.
     *
     * @param productName 변경할 상품 명칭
     * @param productCode 변경할 상품 코드
     */
    public void update(String productName, String productCode) {
        this.productName = productName;
        this.productCode = productCode;
    }
}
