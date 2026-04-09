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
 * OCSA(Operational Complexity & Skill Assessment) 난이도 산출에 사용하는 가중치 설정 엔티티.
 * 산업군 프리셋(IndustryPreset)별로 V1~V4 지표 가중치와 신규성(α) 가중치를 저장하며,
 * Order 엔티티의 config_id 에서 참조한다.
 * 적용 시작일(effectiveDate) 기준으로 여러 버전의 가중치를 관리할 수 있다.
 */
@Entity
@Table(name = "OCSA_weight_config")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OcsaWeightConfig {

    @Id
    @Column(name = "config_id")
    private Long configId; // 가중치 설정 PK

    @Enumerated(EnumType.STRING)
    @Column(name = "industry_preset", nullable = false)
    private IndustryPreset industryPreset; // 적용 대상 산업군 프리셋

    @Column(name = "weight_v1")
    private BigDecimal weightV1; // V1(공정 복잡도) 가중치

    @Column(name = "weight_v2")
    private BigDecimal weightV2; // V2(품질 정밀도) 가중치

    @Column(name = "weight_v3")
    private BigDecimal weightV3; // V3(설비 역량 요구도) 가중치

    @Column(name = "weight_v4")
    private BigDecimal weightV4; // V4(공간·시간 긴급도) 가중치

    @Column(name = "alpha_weight")
    private BigDecimal alphaWeight; // α(신규성 보정 계수) 가중치

    @Column(name = "effective_date")
    private LocalDate effectiveDate; // 해당 가중치 설정의 적용 시작일

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
}
