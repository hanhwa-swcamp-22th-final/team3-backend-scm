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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * SCM 도메인의 핵심 집합체(Aggregate Root).
 * 고객 주문 한 건을 표현하며, OCSA 난이도 분석 결과와 처리 상태를 함께 관리한다.
 * 상태 흐름: REGISTERED → ANALYZED → INPROGRESS → COMPLETED
 */
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Column(name = "order_id")
    private Long orderId; // 주문 PK

    @Column(name = "product_id", nullable = false)
    private Long productId; // 주문 대상 제품 ID (Product 테이블 참조)

    @Column(name = "config_id", nullable = false)
    private Long configId; // OCSA 가중치 설정 ID (OcsaWeightConfig 테이블 참조)

    @Column(name = "order_no")
    private String orderNumber; // 사람이 읽을 수 있는 주문 번호 (예: ORD-20240101-001)

    @Column(name = "order_quantity")
    private Integer orderQuantity; // 주문 수량

    @Column(name = "order_deadline", nullable = false)
    private LocalDate dueDate; // 납기 마감일

    @Column(name = "process_step_count")
    private Integer processStepCount; // V1 input snapshot

    @Column(name = "tolerance_mm", precision = 8, scale = 4)
    private BigDecimal toleranceMm; // V2 input snapshot

    @Column(name = "skill_level")
    private Integer skillLevel; // V3 input snapshot

    @Column(name = "is_first_order")
    private Boolean isFirstOrder; // 해당 제품의 최초 주문 여부 (alpha 신규도 반영에 사용)

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status; // 주문 처리 상태

    // ── OCSA 난이도 구성 지표 (Operational Complexity & Skill Assessment) ──

    @Column(name = "v1_process_complexity")
    private BigDecimal v1ProcessComplexity; // V1: 공정 복잡도

    @Column(name = "v2_quality_precision")
    private BigDecimal v2QualityPrecision; // V2: 품질 정밀도

    @Column(name = "v3_capacity_requirements")
    private BigDecimal v3CapacityRequirements; // V3: 역량 요구도 (legacy column name kept for compatibility)

    @Column(name = "v4_space_time_urgency")
    private BigDecimal v4SpaceTimeUrgency; // V4: 공간·시간 긴급도

    @Column(name = "alpha_novelty")
    private BigDecimal alphaNovlety; // α: 신규도 보정 계수 (최초 주문일수록 높음)

    @Column(name = "difficulty_score")
    private BigDecimal difficultyScore; // OCSA 가중합으로 산출된 최종 난이도 점수

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_grade")
    private DifficultyGrade difficultyGrade; // 난이도 등급 (D1~D5)

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

    /** 순수 Java 테스트 전용 — DB 컬럼 없음 */
    @Transient
    private String itemName; // 테스트에서 제품명을 직접 주입할 때 사용

    /**
     * 일반 생성자 — 초기 상태를 REGISTERED 로 설정한다.
     *
     * @param id          주문 PK
     * @param orderNumber 주문 번호
     * @param itemName    제품명 (테스트용 Transient 필드)
     * @param dueDate     납기 마감일
     */
    public Order(Long id, String orderNumber, String itemName, LocalDate dueDate) {
        this.orderId = id;
        this.orderNumber = orderNumber;
        this.itemName = itemName;
        this.dueDate = dueDate;
        this.status = OrderStatus.REGISTERED;
    }

    /**
     * 테스트용 생성자 — 상태를 직접 지정한다.
     *
     * @param id          주문 PK
     * @param orderNumber 주문 번호
     * @param status      주문 상태
     * @param dueDate     납기 마감일
     */
    public Order(Long id, String orderNumber, OrderStatus status, LocalDate dueDate) {
        this.orderId = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.dueDate = dueDate;
    }

    /**
     * 테스트용 생성자 — 상태와 난이도 등급을 직접 지정한다.
     *
     * @param id              주문 PK
     * @param orderNumber     주문 번호
     * @param status          주문 상태
     * @param dueDate         납기 마감일
     * @param difficultyGrade 난이도 등급
     */
    public Order(Long id, String orderNumber, OrderStatus status, LocalDate dueDate, DifficultyGrade difficultyGrade) {
        this.orderId = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.dueDate = dueDate;
        this.difficultyGrade = difficultyGrade;
    }

    /**
     * REGISTERED → ANALYZED 상태 전이.
     * 주문 분석이 완료되어 기술자 배정 대기 상태로 변경한다.
     */
    public void moveToQueued() {
        this.status = OrderStatus.ANALYZED;
    }

    /**
     * 기술자 배정 확정 — ANALYZED 상태에서만 허용되며, 상태를 INPROGRESS 로 변경한다.
     * ANALYZED 가 아닌 상태에서 호출 시 IllegalStateException 이 발생한다.
     *
     * @param technicianId 배정할 기술자(employee_id)
     * @throws IllegalStateException 주문 상태가 ANALYZED 가 아닐 경우
     */
    public void assignTechnician(Long technicianId) {
        if (this.status != OrderStatus.ANALYZED) {
            throw new IllegalStateException("ANALYZED 상태의 주문에만 기술자를 배정할 수 있습니다. 현재 상태: " + this.status);
        }
        this.status = OrderStatus.INPROGRESS;
    }

    /** 작업 완료 — INPROGRESS 상태에서만 허용, 상태를 COMPLETED 로 변경 */
    public void complete() {
        if (this.status != OrderStatus.INPROGRESS) {
            throw new IllegalStateException("INPROGRESS 상태의 주문만 완료 처리할 수 있습니다. 현재 상태: " + this.status);
        }
        this.status = OrderStatus.COMPLETED;
    }

    /** 배정 취소 — INPROGRESS 상태에서 ANALYZED 로 롤백 */
    public void cancelAssignment() {
        if (this.status != OrderStatus.INPROGRESS) {
            throw new IllegalStateException("INPROGRESS 상태의 주문만 배정 취소할 수 있습니다. 현재 상태: " + this.status);
        }
        this.status = OrderStatus.ANALYZED;
    }

    /**
     * Admin이 주문을 최초 등록할 때 사용하는 정적 팩토리 메서드.
     * 초기 상태는 항상 REGISTERED로 고정된다.
     *
     * @param id           주문 PK (IdGenerator로 생성)
     * @param productId    주문 대상 제품 ID
     * @param configId     OCSA 가중치 설정 ID
     * @param orderNumber  사람이 읽을 수 있는 주문 번호
     * @param quantity     주문 수량
     * @param dueDate      납기 마감일
     * @param isFirstOrder 해당 제품의 최초 주문 여부
     * @return 생성된 Order 인스턴스 (REGISTERED 상태)
     */
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
        return order;
    }

    /**
     * Admin이 주문 기본 정보를 수정한다.
     * REGISTERED 상태에서만 허용하며, SCM 워크플로우가 시작된 이후에는 수정 불가하다.
     *
     * @param productId   변경할 제품 ID
     * @param orderNumber 변경할 주문 번호
     * @param quantity    변경할 주문 수량
     * @param dueDate     변경할 납기 마감일
     * @throws IllegalStateException REGISTERED 상태가 아닐 경우
     */
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
            throw new IllegalStateException("REGISTERED 상태의 주문만 수정할 수 있습니다. 현재 상태: " + this.status);
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
        if (this.status != OrderStatus.REGISTERED) {
            throw new IllegalStateException("REGISTERED 상태의 주문만 수정할 수 있습니다. 현재 상태: " + this.status);
        }
        this.productId = productId;
        this.orderNumber = orderNumber;
        this.orderQuantity = quantity;
        this.dueDate = dueDate;
    }

    /**
     * 납기 3일 이내이면 긴급 주문으로 판단한다.
     *
     * @return 납기가 오늘로부터 3일 이내이면 true
     */
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
            throw new IllegalStateException("REGISTERED 또는 ANALYZED 상태의 주문만 난이도 분석 결과를 반영할 수 있습니다. 현재 상태: " + this.status);
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
