package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "matching_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingRecord {

    @Id
    @Column(name = "matching_record_id")
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "matching_status", nullable = false)
    private MatchingStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** 순수 Java 테스트 전용 — DB 컬럼 없음 */
    @Transient
    private LocalDate assignedDate;

    public MatchingRecord(Long id, Long orderId, Long technicianId, LocalDate assignedDate) {
        this.id = id;
        this.orderId = orderId;
        this.employeeId = technicianId;
        this.assignedDate = assignedDate;
        this.status = MatchingStatus.CONFIRM;
        this.createdAt = assignedDate.atStartOfDay();
    }

    /** 동일 기술자·동일 날짜 중복 배정 방지 (순수 Java 테스트용) */
    public void validateNotDuplicated(Long technicianId, LocalDate date) {
        if (this.employeeId.equals(technicianId)
                && this.assignedDate != null
                && this.assignedDate.equals(date)) {
            throw new IllegalStateException("이미 해당 날짜에 배정된 기술자입니다. employeeId=" + technicianId);
        }
    }
}
