package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ?¤ë¹„ ?˜ê²½ ?´ìƒ ê°ì? ?¸ë Œ???°ì´?°ë? ?´ëŠ” ?‘ë‹µ DTO.
 * ?¹ì • ?¤ë¹„?ì„œ ê°ì????¨ë„, ?µë„, ?Œí‹°???˜ì¹˜ ?´ìƒ ?´ë²¤?¸ë? ?œê³„?´ë¡œ ?œê³µ?˜ì—¬
 * ?¤ë¹„ ?íƒœ ëª¨ë‹ˆ?°ë§ ë°??´ìƒ ?¨í„´ ë¶„ì„???¬ìš©?œë‹¤.
 * GET /api/v1/scm/facilities/{facilityId}/trends ?ì„œ ë°˜í™˜?œë‹¤.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityTrendsDto {

    private Long equipmentId;          // ?¤ë¹„(?¥ë¹„) PK
    private LocalDateTime detectedAt;  // ?´ìƒ ê°ì? ?¼ì‹œ
    private BigDecimal temperature;    // ê°ì? ?¹ì‹œ ?¨ë„ (??
    private BigDecimal humidity;       // ê°ì? ?¹ì‹œ ?µë„ (%)
    private Integer particleCnt;       // ê°ì? ?¹ì‹œ ?Œí‹°??ë¨¼ì?) ??
    private String deviationType;      // ?´ìƒ ? í˜• (?? TEMP_HIGH, HUMID_LOW, PARTICLE_HIGH)
}
