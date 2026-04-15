package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.AdminFeignClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.AdminEmployeeProfileResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilitySummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.FacilityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FacilityQueryServiceTest {

    @Mock
    private FacilityMapper facilityMapper;
    @Mock
    private AdminFeignClient adminFeignClient;

    @InjectMocks
    private FacilityQueryService facilityQueryService;

    @Test
    @DisplayName("설비 목록 조회 시 Mapper가 1회 호출된다")
    void getFacilities_CallsMapperOnce() {
        // given
        given(facilityMapper.findFacilities()).willReturn(List.of());

        // when
        facilityQueryService.getFacilities();

        // then
        verify(facilityMapper, times(1)).findFacilities();
    }

    @Test
    @DisplayName("설비 이력 조회 시 Mapper가 1회 호출된다")
    void getFacilityHistory_CallsMapperOnce() {
        // given
        given(facilityMapper.findFacilityHistory(anyLong())).willReturn(List.of());

        // when
        facilityQueryService.getFacilityHistory(1L);

        // then
        verify(facilityMapper, times(1)).findFacilityHistory(anyLong());
    }

    @Test
    @DisplayName("설비 배치 기술자 조회 시 Admin Feign으로 직원명을 병합한다")
    void getFacilityDeployments_MergesEmployeeNameFromAdmin() {
        // given
        given(facilityMapper.findFacilityDeployments(anyLong()))
                .willReturn(List.of(new FacilityDeploymentDto(10L, null, LocalDate.now())));
        AdminEmployeeProfileResponse profile = new AdminEmployeeProfileResponse();
        profile.setEmployeeId(10L);
        profile.setEmployeeName("김작업");
        given(adminFeignClient.getEmployeeProfile(10L)).willReturn(ApiResponse.success(profile));

        // when
        List<FacilityDeploymentDto> result = facilityQueryService.getFacilityDeployments(1L);

        // then
        assertEquals("김작업", result.get(0).getEmployeeName());
        verify(facilityMapper, times(1)).findFacilityDeployments(anyLong());
    }

    @Test
    @DisplayName("설비 요약 조회 시 Mapper가 1회 호출된다")
    void getFacilitySummary_CallsMapperOnce() {
        // given
        given(facilityMapper.findFacilitySummary()).willReturn(new FacilitySummaryDto());

        // when
        facilityQueryService.getFacilitySummary();

        // then
        verify(facilityMapper, times(1)).findFacilitySummary();
    }

    @Test
    @DisplayName("설비 추이 조회 시 Mapper가 1회 호출된다")
    void getFacilityTrends_CallsMapperOnce() {
        // given
        given(facilityMapper.findFacilityTrends(anyLong())).willReturn(List.of());

        // when
        facilityQueryService.getFacilityTrends(1L);

        // then
        verify(facilityMapper, times(1)).findFacilityTrends(anyLong());
    }
}
