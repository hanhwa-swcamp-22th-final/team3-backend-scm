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
 * 주문 대상이 되는 제품(Product) 엔티티.
 * 제품 이름·코드를 관리하며, Order 엔티티에서 product_id 로 참조된다.
 */
@Entity
@Table(name = "product")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @Column(name = "product_id")
    private Long productId; // 제품 PK

    @Column(name = "product_name", nullable = false)
    private String productName; // 제품 명칭

    @Column(name = "product_code", nullable = false)
    private String productCode; // 제품 코드 (고유 식별 코드)

    // ── JPA Auditing 자동 채워짐 ──

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
}
