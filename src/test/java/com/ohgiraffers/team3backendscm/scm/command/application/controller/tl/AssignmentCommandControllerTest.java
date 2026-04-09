package com.ohgiraffers.team3backendscm.scm.command.application.controller.tl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.team3backendscm.scm.command.application.controller.tl.AssignmentCommandController;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.AssignRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ReassignRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.tl.AssignmentCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.ohgiraffers.team3backendscm.jwt.JwtTokenProvider;
import com.ohgiraffers.team3backendscm.jwt.RestAccessDeniedHandler;
import com.ohgiraffers.team3backendscm.jwt.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(AssignmentCommandController.class)
class AssignmentCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private AssignmentCommandService assignmentCommandService;
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private RestAccessDeniedHandler restAccessDeniedHandler;
    @MockBean private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/scm/assignments → 200 OK + ApiResponse.success")
    void createAssignment_Return200() throws Exception {
        // given
        AssignRequest request = new AssignRequest(1L, 10L);
        doNothing().when(assignmentCommandService).assign(any());

        // when & then
        mockMvc.perform(post("/api/v1/scm/assignments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/scm/assignments/1 → 200 OK + ApiResponse.success")
    void reassignAssignment_Return200() throws Exception {
        // given
        ReassignRequest request = new ReassignRequest(20L);
        doNothing().when(assignmentCommandService).reassign(anyLong(), any());

        // when & then
        mockMvc.perform(put("/api/v1/scm/assignments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/scm/assignments/1 → 200 OK + ApiResponse.success")
    void cancelAssignment_Return200() throws Exception {
        // given
        doNothing().when(assignmentCommandService).cancel(anyLong());

        // when & then
        mockMvc.perform(delete("/api/v1/scm/assignments/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
