package com.ohgiraffers.team3backendscm.scm.query.controller.tl;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineStatusDto;
import com.ohgiraffers.team3backendscm.scm.query.service.tl.LineQueryService;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(LineQueryController.class)
class LineQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private LineQueryService lineQueryService;
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private RestAccessDeniedHandler restAccessDeniedHandler;
    @MockBean private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Test
    @DisplayName("GET /api/v1/scm/lines/summary → 200 OK")
    void getLinesSummary_Return200() throws Exception {
        // given
        given(lineQueryService.getLinesSummary()).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/lines/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/lines/1/status → 200 OK")
    void getLineStatus_Return200() throws Exception {
        // given
        given(lineQueryService.getLineStatus(anyLong())).willReturn(new LineStatusDto());

        // when / then
        mockMvc.perform(get("/api/v1/scm/lines/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
