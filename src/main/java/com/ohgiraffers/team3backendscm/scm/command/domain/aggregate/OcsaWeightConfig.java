package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "`OCSA_weight_config`")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OcsaWeightConfig {

    @Id
    @Column(name = "config_id")
    private Long configId;

    @Enumerated(EnumType.STRING)
    @Column(name = "industry_preset_name", nullable = false)
    private IndustryPreset industryPreset;

    @Column(name = "weight_v1")
    private BigDecimal weightV1;

    @Column(name = "weight_v2")
    private BigDecimal weightV2;

    @Column(name = "weight_v3")
    private BigDecimal weightV3;

    @Column(name = "weight_v4")
    private BigDecimal weightV4;

    @Column(name = "alpha_weight")
    private BigDecimal alphaWeight;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

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
}
