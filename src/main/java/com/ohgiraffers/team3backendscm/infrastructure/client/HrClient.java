package com.ohgiraffers.team3backendscm.infrastructure.client;

import com.ohgiraffers.team3backendscm.infrastructure.client.dto.HrTeamMemberResponse;

import java.util.List;

/**
 * HR 모듈 클라이언트 계약 인터페이스.
 * HTTP/Feign 세부 구현을 숨기고 서비스 레이어에 순수 비즈니스 메서드만 노출한다.
 */
public interface HrClient {

    /**
     * 현재 인증된 팀리더의 팀원 목록을 조회한다.
     */
    List<HrTeamMemberResponse> getTeamMembers();
}
