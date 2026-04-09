package com.ohgiraffers.team3backendscm.scm.query.controller.tl;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.service.tl.AssignmentQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.ohgiraffers.team3backendscm.jwt.JwtTokenProvider;
import com.ohgiraffers.team3backendscm.jwt.RestAccessDeniedHandler;
import com.ohgiraffers.team3backendscm.jwt.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(AssignmentQueryController.class)
class AssignmentQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private AssignmentQueryService assignmentQueryService;
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private RestAccessDeniedHandler restAccessDeniedHandler;
    @MockBean private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Test
    @DisplayName("GET /api/v1/scm/assignments/1 → 200 OK + 배정 상세 반환")
    void getAssignment_Return200() throws Exception {
        // given
        given(assignmentQueryService.getAssignment(1L)).willReturn(new AssignmentDetailDto());

        // when / then
        mockMvc.perform(get("/api/v1/scm/assignments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/assignments/candidates → 200 OK")
    void getCandidates_Return200() throws Exception {
        // given
        given(assignmentQueryService.getCandidates()).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/assignments/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/assignments/summary → 200 OK")
    void getSummary_Return200() throws Exception {
        // given
        given(assignmentQueryService.getSummary()).willReturn(new AssignmentSummaryDto());

        // when / then
        mockMvc.perform(get("/api/v1/scm/assignments/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/assignments/timeline → 200 OK")
    void getTimeline_Return200() throws Exception {
        // given
        given(assignmentQueryService.getTimeline()).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/assignments/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/assignments/rebalance → 200 OK")
    void getRebalance_Return200() throws Exception {
        // given
        given(assignmentQueryService.getRebalance()).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/assignments/rebalance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
