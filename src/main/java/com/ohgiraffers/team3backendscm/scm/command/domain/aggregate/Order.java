package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Column(name = "order_id")
    private Long id;

    @Column(name = "order_no")
    private String orderNumber;

    /** 순수 Java 테스트 전용 — DB 컬럼 없음 */
    @Transient
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status;

    @Column(name = "order_deadline", nullable = false)
    private LocalDate dueDate;

    /** 일반 생성 — 초기 상태 REGISTERED */
    public Order(Long id, String orderNumber, String itemName, LocalDate dueDate) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.itemName = itemName;
        this.dueDate = dueDate;
        this.status = OrderStatus.REGISTERED;
    }

    /** 테스트용 생성 — 상태 직접 지정 */
    public Order(Long id, String orderNumber, OrderStatus status, LocalDate dueDate) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.dueDate = dueDate;
    }

    /** REGISTERED → ANALYZED 상태 전이 (배정 대기) */
    public void moveToQueued() {
        this.status = OrderStatus.ANALYZED;
    }

    /** 기술자 배정 확정 — ANALYZED 상태에서만 허용, 상태를 INPROGRESS 로 변경 */
    public void assignTechnician(Long technicianId) {
        if (this.status != OrderStatus.ANALYZED) {
            throw new IllegalStateException("ANALYZED 상태의 주문에만 기술자를 배정할 수 있습니다. 현재 상태: " + this.status);
        }
        this.status = OrderStatus.INPROGRESS;
    }

    /** 납기 3일 이내이면 긴급 주문 */
    public boolean isUrgent() {
        return !dueDate.isAfter(LocalDate.now().plusDays(3));
    }
}
