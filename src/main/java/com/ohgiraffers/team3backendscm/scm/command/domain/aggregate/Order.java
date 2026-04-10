package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "config_id", nullable = false)
    private Long configId;

    @Column(name = "order_no")
    private String orderNumber;

    @Column(name = "order_quantity")
    private Integer orderQuantity;

    @Column(name = "order_deadline", nullable = false)
    private LocalDate dueDate;

    @Column(name = "process_step_count")
    private Integer processStepCount;

    @Column(name = "tolerance_mm", precision = 8, scale = 4)
    private BigDecimal toleranceMm;

    @Column(name = "skill_level")
    private Integer skillLevel;

    @Column(name = "is_first_order")
    private Boolean isFirstOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status;

    @Column(name = "v1_process_complexity")
    private BigDecimal v1ProcessComplexity;

    @Column(name = "v2_quality_precision")
    private BigDecimal v2QualityPrecision;

    @Column(name = "v3_capacity_requirements")
    private BigDecimal v3CapacityRequirements; // legacy column name kept for compatibility

    @Column(name = "v4_space_time_urgency")
    private BigDecimal v4SpaceTimeUrgency;

    @Column(name = "alpha_novelty")
    private BigDecimal alphaNovlety;

    @Column(name = "difficulty_score")
    private BigDecimal difficultyScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_grade")
    private DifficultyGrade difficultyGrade;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    @Transient
    private String itemName;

    public Order(Long id, String orderNumber, String itemName, LocalDate dueDate) {
        this.orderId = id;
        this.orderNumber = orderNumber;
        this.itemName = itemName;
        this.dueDate = dueDate;
        this.status = OrderStatus.REGISTERED;
    }

    public Order(Long id, String orderNumber, OrderStatus status, LocalDate dueDate) {
        this.orderId = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.dueDate = dueDate;
    }

    public Order(Long id, String orderNumber, OrderStatus status, LocalDate dueDate, DifficultyGrade difficultyGrade) {
        this.orderId = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.dueDate = dueDate;
        this.difficultyGrade = difficultyGrade;
    }

    public void moveToQueued() {
        this.status = OrderStatus.ANALYZED;
    }

    public void assignTechnician(Long technicianId) {
        if (this.status != OrderStatus.ANALYZED) {
            throw new IllegalStateException("Only ANALYZED orders can be assigned. Current status: " + this.status);
        }
        this.status = OrderStatus.INPROGRESS;
    }

    public void complete() {
        if (this.status != OrderStatus.INPROGRESS) {
            throw new IllegalStateException("Only INPROGRESS orders can be completed. Current status: " + this.status);
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void cancelAssignment() {
        if (this.status != OrderStatus.INPROGRESS) {
            throw new IllegalStateException("Only INPROGRESS orders can cancel assignment. Current status: " + this.status);
        }
        this.status = OrderStatus.ANALYZED;
    }

    public static Order register(Long id, Long productId, Long configId, String orderNumber,
                                 Integer quantity, LocalDate dueDate, Boolean isFirstOrder) {
        return register(id, productId, configId, orderNumber, quantity, dueDate, 1, new BigDecimal("0.1000"), 1, isFirstOrder);
    }

    public static Order register(Long id, Long productId, Long configId, String orderNumber,
                                 Integer quantity, LocalDate dueDate, Integer processStepCount,
                                 BigDecimal toleranceMm, Integer skillLevel, Boolean isFirstOrder) {
        Order order = new Order();
        order.orderId = id;
        order.productId = productId;
        order.configId = configId;
        order.orderNumber = orderNumber;
        order.orderQuantity = quantity;
        order.dueDate = dueDate;
        order.processStepCount = processStepCount;
        order.toleranceMm = toleranceMm;
        order.skillLevel = skillLevel;
        order.isFirstOrder = isFirstOrder;
        order.status = OrderStatus.REGISTERED;
        order.difficultyGrade = DifficultyGrade.D1;
        return order;
    }

    public void updateInfo(
        Long productId,
        String orderNumber,
        Integer quantity,
        LocalDate dueDate,
        Integer processStepCount,
        BigDecimal toleranceMm,
        Integer skillLevel
    ) {
        if (this.status != OrderStatus.REGISTERED) {
            throw new IllegalStateException("Only REGISTERED orders can be updated. Current status: " + this.status);
        }
        this.productId = productId;
        this.orderNumber = orderNumber;
        this.orderQuantity = quantity;
        this.dueDate = dueDate;
        this.processStepCount = processStepCount;
        this.toleranceMm = toleranceMm;
        this.skillLevel = skillLevel;
    }

    public void updateInfo(Long productId, String orderNumber, Integer quantity, LocalDate dueDate) {
        updateInfo(productId, orderNumber, quantity, dueDate, this.processStepCount, this.toleranceMm, this.skillLevel);
    }

    public void applyDifficultyAnalysis(
        BigDecimal v1ProcessComplexity,
        BigDecimal v2QualityPrecision,
        BigDecimal v3CapacityRequirements,
        BigDecimal v4SpaceTimeUrgency,
        BigDecimal alphaNovelty,
        BigDecimal difficultyScore,
        DifficultyGrade difficultyGrade
    ) {
        if (this.status != OrderStatus.REGISTERED && this.status != OrderStatus.ANALYZED) {
            throw new IllegalStateException(
                "Only REGISTERED or ANALYZED orders can apply difficulty analysis. Current status: " + this.status
            );
        }

        this.v1ProcessComplexity = v1ProcessComplexity;
        this.v2QualityPrecision = v2QualityPrecision;
        this.v3CapacityRequirements = v3CapacityRequirements;
        this.v4SpaceTimeUrgency = v4SpaceTimeUrgency;
        this.alphaNovlety = alphaNovelty;
        this.difficultyScore = difficultyScore;
        this.difficultyGrade = difficultyGrade;
        this.status = OrderStatus.ANALYZED;
    }

    public boolean isUrgent() {
        return !dueDate.isAfter(LocalDate.now().plusDays(3));
    }
}
