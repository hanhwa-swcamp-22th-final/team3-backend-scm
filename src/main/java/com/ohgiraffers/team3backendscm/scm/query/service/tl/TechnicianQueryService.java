package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.infrastructure.client.AdminClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.HrClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.AdminEmployeeProfileResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.TechnicianDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 팀 리더(TL) 권한의 기술자 조회 Query 서비스.
 */
@Service
@RequiredArgsConstructor
public class TechnicianQueryService {

    private static final List<String> TIERS = List.of("S", "A", "B", "C");

    private final AdminClient adminClient;
    private final HrClient hrClient;

    /**
     * 배정 가능한 전체 기술자 목록을 조회한다.
     *
     * @return 기술자 목록(이름, 티어, OCSA 점수, 적합도 포함)
     */
    public List<TechnicianDto> getTechnicians() {
        return getVisibleWorkerIds().stream()
                .distinct()
                .map(this::getProfile)
                .filter(Objects::nonNull)
                .map(profile -> new TechnicianDto(
                        profile.getEmployeeId(),
                        profile.getEmployeeName(),
                        profile.getCurrentTier(),
                        profile.getTotalScore(),
                        null
                ))
                .sorted(Comparator.comparing(
                        TechnicianDto::getOcsaScore,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    private List<Long> getVisibleWorkerIds() {
        List<Long> teamMemberIds = hrClient.getTeamMembers().stream()
                .map(member -> member.getEmployeeId())
                .toList();
        if (!teamMemberIds.isEmpty()) {
            return teamMemberIds;
        }

        return TIERS.stream()
                .flatMap(tier -> adminClient.getActiveWorkerIdsByTier(tier).stream())
                .toList();
    }

    private AdminEmployeeProfileResponse getProfile(Long employeeId) {
        return adminClient.getEmployeeProfile(employeeId);
    }
}
