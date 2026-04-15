package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.AdminFeignClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.AdminEmployeeProfileResponse;
import com.ohgiraffers.team3backendscm.jwt.EmployeeUserDetails;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.TechnicianDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TechnicianQueryServiceTest {

    @Mock
    private AdminFeignClient adminFeignClient;

    @InjectMocks
    private TechnicianQueryService technicianQueryService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("TL 팀원 ID를 Admin Feign으로 조회해 작업자 목록을 조립한다")
    void getTechnicians_UsesTeamMemberIds() {
        setAuthenticatedUser(100L, "TL");
        given(adminFeignClient.getTeamMemberIds(100L)).willReturn(ApiResponse.success(List.of(10L)));
        given(adminFeignClient.getEmployeeProfile(10L)).willReturn(ApiResponse.success(profile(10L, "김작업", "A", "92.5")));

        List<TechnicianDto> result = technicianQueryService.getTechnicians();

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getEmployeeId());
        assertEquals("김작업", result.get(0).getEmployeeName());
        assertEquals("A", result.get(0).getTier());
        assertEquals(new BigDecimal("92.5"), result.get(0).getOcsaScore());
        verify(adminFeignClient, never()).getActiveWorkerIdsByTier("S");
    }

    private void setAuthenticatedUser(Long employeeId, String role) {
        EmployeeUserDetails userDetails = new EmployeeUserDetails(
                employeeId,
                "EMP" + employeeId,
                List.of(new SimpleGrantedAuthority(role))
        );
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
    }

    private AdminEmployeeProfileResponse profile(Long employeeId, String name, String tier, String score) {
        AdminEmployeeProfileResponse profile = new AdminEmployeeProfileResponse();
        profile.setEmployeeId(employeeId);
        profile.setEmployeeName(name);
        profile.setCurrentTier(tier);
        profile.setTotalScore(new BigDecimal(score));
        return profile;
    }
}
