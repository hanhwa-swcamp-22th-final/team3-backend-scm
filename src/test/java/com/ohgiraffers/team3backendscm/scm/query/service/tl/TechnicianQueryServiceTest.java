package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.infrastructure.client.AdminClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.HrClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.AdminEmployeeProfileResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.HrTeamMemberResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.TechnicianDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TechnicianQueryServiceTest {

    @Mock
    private AdminClient adminClient;
    @Mock
    private HrClient hrClient;

    @InjectMocks
    private TechnicianQueryService technicianQueryService;

    @Test
    @DisplayName("HR 팀원 ID 기준으로 작업자 목록을 조립한다")
    void getTechnicians_UsesTeamMemberIds() {
        HrTeamMemberResponse member = mock(HrTeamMemberResponse.class);
        given(member.getEmployeeId()).willReturn(10L);
        given(hrClient.getTeamMembers()).willReturn(List.of(member));
        given(adminClient.getEmployeeProfile(10L)).willReturn(profile(10L, "김작업", "A", "92.5"));

        List<TechnicianDto> result = technicianQueryService.getTechnicians();

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getEmployeeId());
        assertEquals("김작업", result.get(0).getEmployeeName());
        assertEquals("A", result.get(0).getTier());
        assertEquals(new BigDecimal("92.5"), result.get(0).getOcsaScore());
        verify(adminClient, never()).getActiveWorkerIdsByTier("S");
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
