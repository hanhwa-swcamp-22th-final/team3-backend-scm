package com.ohgiraffers.team3backendscm.scm.query.controller.worker;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.TaskDto;
import com.ohgiraffers.team3backendscm.scm.query.service.worker.WorkerQueryService;
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
@WebMvcTest(WorkerQueryController.class)
class WorkerQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkerQueryService workerQueryService;

    @Test
    @DisplayName("GET /api/v1/scm/tasks?employeeId=10 → 200 OK + 미완료 작업 목록 반환")
    void getMyPendingTasks_Return200() throws Exception {
        // given
        given(workerQueryService.getMyPendingTasks(anyLong())).willReturn(List.of(new TaskDto()));

        // when / then
        mockMvc.perform(get("/api/v1/scm/tasks").param("employeeId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/workers/me/deployments?employeeId=10 → 200 OK")
    void getMyDeployments_Return200() throws Exception {
        // given
        given(workerQueryService.getMyDeployments(anyLong())).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/workers/me/deployments").param("employeeId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/workers/me/matching-history?employeeId=10 → 200 OK")
    void getMyMatchingHistory_Return200() throws Exception {
        // given
        given(workerQueryService.getMyMatchingHistory(anyLong())).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/workers/me/matching-history").param("employeeId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
