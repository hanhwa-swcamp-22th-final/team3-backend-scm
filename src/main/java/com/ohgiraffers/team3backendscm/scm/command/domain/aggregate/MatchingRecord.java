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
import java.time.LocalDateTime;

/**
 * 기술자(Technician)와 주문(Order) 간의 배정 매칭 기록 엔티티.
 * 팀 리더가 특정 주문에 기술자를 배정할 때마다 한 건씩 생성된다.
 * 배정 방식(MatchingMode), 기대 결과 지표, 품질 리스크를 함께 저장한다.
 */
@Entity
@Table(name = "matching_record")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingRecord {

    @Id
    @Column(name = "matching_record_id")
    private Long matchingRecordId; // 배정 기록 PK

    @Column(name = "employee_id", nullable = false)
    private Long employeeId; // 배정된 기술자(직원) ID

    @Column(name = "order_id", nullable = false)
    private Long orderId; // 배정 대상 주문 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "matching_mode")
    private MatchingMode matchingMode; // 배정 방식 (GROWTH_TYPE / EFFICIENCY_TYPE)

    @Enumerated(EnumType.STRING)
    @Column(name = "matching_status", nullable = false)
    private MatchingStatus status; // 배정 처리 상태 (RECOMMEND 또는 CONFIRM 등)

    @Column(name = "d_c_ratio")
    private BigDecimal dcRatio; // 난이도 대비 숙련도 비율 (Difficulty-Capability Ratio)

    @Column(name = "expected_bonus")
    private BigDecimal expectedBonus; // 배정 시 산정된 기대 보너스 금액

    @Column(name = "expected_productivity")
    private BigDecimal expectedProductivity; // 배정 기술자의 기대 생산성 지표

    @Column(name = "quality_risk")
    private BigDecimal qualityRisk; // 품질 불량 발생 위험 지표

    @Column(name = "work_start_at")
    private LocalDateTime workStartAt; // 작업 시작 일시

    @Column(name = "work_end_at")
    private LocalDateTime workEndAt; // 작업 종료 일시

    @Column(name = "comment", length = 500)
    private String comment; // 배정 코멘트

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
     * 배정 확정 기록 생성 생성자.
     * 상태를 CONFIRM 으로 초기화한다.
     *
     * @param id            배정 기록 PK
     * @param orderId       배정 대상 주문 ID
     * @param technicianId  배정된 기술자(employee_id)
     * @param matchingMode  배정 방식
     */
    public MatchingRecord(Long id, Long orderId, Long technicianId, MatchingMode matchingMode) {
        this.matchingRecordId = id;
        this.orderId = orderId;
        this.employeeId = technicianId;
        this.matchingMode = matchingMode;
        this.status = MatchingStatus.CONFIRM;
    }

    /** 재배치 시 기술자 ID와 매칭 모드를 새 값으로 변경한다. */
    public void reassign(Long newTechnicianId, MatchingMode newMatchingMode) {
        this.employeeId = newTechnicianId;
        this.matchingMode = newMatchingMode;
    }

    /** 배정 취소 시 완료(COMPLETE)된 배정은 취소 불가 */
    public void cancel() {
        if (this.status == MatchingStatus.COMPLETE) {
            throw new IllegalStateException("완료된 배정은 취소할 수 없습니다.");
        }
        this.status = MatchingStatus.REJECT;
    }

    /** 작업 시작 시 workStartAt 을 현재 시각으로 설정한다. 이미 시작된 경우 예외. */
    public void startWork() {
        if (this.workStartAt != null) {
            throw new IllegalStateException("이미 시작된 작업입니다.");
        }
        this.workStartAt = LocalDateTime.now();
    }

    /** 작업 종료 일시를 설정한다. workEndAt·comment 를 기록하되 상태는 유지한다. */
    public void finishDraft(String comment) {
        this.workEndAt = LocalDateTime.now();
        this.comment = comment;
    }

    /** 작업 종료 호출 시 workEndAt·comment 를 수정하고 상태를 COMPLETE 로 전환한다. */
    public void finish(String comment) {
        this.workEndAt = LocalDateTime.now();
        this.comment = comment;
        this.status = MatchingStatus.COMPLETE;
    }

}
