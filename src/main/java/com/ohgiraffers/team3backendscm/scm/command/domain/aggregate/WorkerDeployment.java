package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 기술자 설비 배치 도메인 엔티티.
 * 특정 기술자가 어느 설비에 언제부터 언제까지 배치되었는지를 기록한다.
 * equipment_id 는 Admin 서비스 소유 설비의 식별자로, 크로스 서비스 참조이므로 단순 Long 으로 보관한다.
 */
@Entity
@Table(name = "worker_deployment")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkerDeployment {

    @Id
    @Column(name = "worker_deployment_id")
    private Long workerDeploymentId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    /** Admin 서비스 소유 설비 PK — FK 제약 없이 ID 값만 보관 */
    @Column(name = "equipment_id", nullable = false)
    private Long equipmentId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** null 이면 현재 배치 중 */
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "role")
    private String role;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 현재 활성 배치 여부 */
    public boolean isActive() {
        return endDate == null || !endDate.isBefore(LocalDate.now());
    }
}
