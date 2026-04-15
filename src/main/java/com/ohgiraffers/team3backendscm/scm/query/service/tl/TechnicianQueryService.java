package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.AdminFeignClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.AdminEmployeeProfileResponse;
import com.ohgiraffers.team3backendscm.jwt.EmployeeUserDetails;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.TechnicianDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final AdminFeignClient adminFeignClient;

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
        Long currentEmployeeId = getCurrentEmployeeId();
        if (currentEmployeeId != null) {
            ApiResponse<List<Long>> response = adminFeignClient.getTeamMemberIds(currentEmployeeId);
            if (response != null && Boolean.TRUE.equals(response.getSuccess())
                    && response.getData() != null && !response.getData().isEmpty()) {
                return response.getData();
            }
        }

        return TIERS.stream()
                .flatMap(tier -> getActiveWorkerIdsByTier(tier).stream())
                .toList();
    }

    private Long getCurrentEmployeeId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof EmployeeUserDetails userDetails)) {
            return null;
        }
        return userDetails.getEmployeeId();
    }

    private List<Long> getActiveWorkerIdsByTier(String tier) {
        ApiResponse<List<Long>> response = adminFeignClient.getActiveWorkerIdsByTier(tier);
        if (response == null || !Boolean.TRUE.equals(response.getSuccess()) || response.getData() == null) {
            return List.of();
        }
        return response.getData();
    }

    private AdminEmployeeProfileResponse getProfile(Long employeeId) {
        ApiResponse<AdminEmployeeProfileResponse> response = adminFeignClient.getEmployeeProfile(employeeId);
        if (response == null || !Boolean.TRUE.equals(response.getSuccess())) {
            return null;
        }
        return response.getData();
    }
}
