package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.TechnicianDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.TechnicianMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 팀 리더(TL) 권한의 기술자 조회 Query 서비스.
 * TechnicianMapper를 통해 배정 가능한 기술자 목록을 읽기 전용으로 제공한다.
 */
@Service
@RequiredArgsConstructor
public class TechnicianQueryService {

    private final TechnicianMapper technicianMapper;

    /**
     * 배정 가능한 전체 기술자 목록을 조회한다.
     *
     * @return 기술자 목록(이름, 티어, OCSA 점수, 적합도 포함)
     */
    public List<TechnicianDto> getTechnicians() {
        return technicianMapper.findTechnicians();
    }
}