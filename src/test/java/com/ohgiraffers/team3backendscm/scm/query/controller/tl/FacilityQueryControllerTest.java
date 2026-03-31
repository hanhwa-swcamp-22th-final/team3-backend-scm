package com.ohgiraffers.team3backendscm.scm.query.controller.tl;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilitySummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.service.tl.FacilityQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(FacilityQueryController.class)
class FacilityQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacilityQueryService facilityQueryService;

    @Test
    @DisplayName("GET /api/v1/scm/facilities → 200 OK")
    void getFacilities_Return200() throws Exception {
        // given
        given(facilityQueryService.getFacilities()).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/facilities/1/history → 200 OK")
    void getFacilityHistory_Return200() throws Exception {
        // given
        given(facilityQueryService.getFacilityHistory(anyLong())).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/facilities/1/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/facilities/1/deployments → 200 OK")
    void getFacilityDeployments_Return200() throws Exception {
        // given
        given(facilityQueryService.getFacilityDeployments(anyLong())).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/facilities/1/deployments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/facilities/summary → 200 OK")
    void getFacilitySummary_Return200() throws Exception {
        // given
        given(facilityQueryService.getFacilitySummary()).willReturn(new FacilitySummaryDto());

        // when / then
        mockMvc.perform(get("/api/v1/scm/facilities/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/facilities/1/trends → 200 OK")
    void getFacilityTrends_Return200() throws Exception {
        // given
        given(facilityQueryService.getFacilityTrends(anyLong())).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/facilities/1/trends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
