package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityHistoryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilitySummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityTrendsDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.FacilityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ?€ ë¦¬ë”(TL) ê¶Œí•œ???¤ë¹„ ì¡°íšŒ Query ?œë¹„??
 * FacilityMapper ë¥??µí•´ ?¤ë¹„ ëª©ë¡, ?´ë ¥, ë°°ì¹˜ ?¸ì›, ?”ì•½, ?¸ë Œ???°ì´?°ë? ?½ê¸° ?„ìš©?¼ë¡œ ?œê³µ?œë‹¤.
 */
@Service
@RequiredArgsConstructor
public class FacilityQueryService {

    private final FacilityMapper facilityMapper;

    /**
     * ?„ì²´ ?¤ë¹„ ëª©ë¡??ì¡°íšŒ?œë‹¤.
     *
     * @return ?¤ë¹„ ê¸°ë³¸ ?•ë³´ ëª©ë¡
     */
    public List<FacilityDto> getFacilities() {
        return facilityMapper.findFacilities();
    }

    /**
     * ?¹ì • ?¤ë¹„???´ë²¤???´ë ¥(?¥ì• , ?ê?, êµì²´ ????ì¡°íšŒ?œë‹¤.
     *
     * @param facilityId ì¡°íšŒ???¤ë¹„ ID
     * @return ?´ë²¤???´ë ¥ ëª©ë¡
     */
    public List<FacilityHistoryDto> getFacilityHistory(Long facilityId) {
        return facilityMapper.findFacilityHistory(facilityId);
    }

    /**
     * ?¹ì • ?¤ë¹„??ë°°ì¹˜??ê¸°ìˆ ??ì§ì›) ?•ë³´ë¥?ì¡°íšŒ?œë‹¤.
     *
     * @param facilityId ì¡°íšŒ???¤ë¹„ ID
     * @return ë°°ì¹˜ ?¸ì› ëª©ë¡
     */
    public List<FacilityDeploymentDto> getFacilityDeployments(Long facilityId) {
        return facilityMapper.findFacilityDeployments(facilityId);
    }

    /**
     * ?„ì²´ ?¤ë¹„ ?íƒœë³?ì§‘ê³„ ?”ì•½??ì¡°íšŒ?œë‹¤.
     *
     * @return ?¤ë¹„ ?„í™© ?”ì•½ DTO
     */
    public FacilitySummaryDto getFacilitySummary() {
        return facilityMapper.findFacilitySummary();
    }

    /**
     * ?¹ì • ?¤ë¹„???˜ê²½ ?´ìƒ ê°ì? ?¸ë Œ???°ì´?°ë? ì¡°íšŒ?œë‹¤.
     *
     * @param facilityId ì¡°íšŒ???¤ë¹„ ID
     * @return ?˜ê²½ ?¸ë Œ???°ì´??ëª©ë¡
     */
    public List<FacilityTrendsDto> getFacilityTrends(Long facilityId) {
        return facilityMapper.findFacilityTrends(facilityId);
    }
}
