package com.ohgiraffers.team3backendscm.scm.query.controller.tl;

import com.ohgiraffers.team3backendscm.scm.query.service.tl.TechnicianQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(TechnicianQueryController.class)
class TechnicianQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TechnicianQueryService technicianQueryService;

    @Test
    @DisplayName("GET /api/v1/scm/technicians → 200 OK")
    void getTechnicians_Return200() throws Exception {
        // given
        given(technicianQueryService.getTechnicians()).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/technicians"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
