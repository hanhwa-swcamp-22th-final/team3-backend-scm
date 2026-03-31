package com.ohgiraffers.team3backendscm.scm.command.application.controller.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.TaskFinishRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.worker.TaskCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 작업자(Worker) 작업 실행 Command 컨트롤러 단위 테스트.
 *
 * <p>테스트 전략: @WebMvcTest — MVC 레이어만 로드하여 HTTP 바인딩과 응답 형식을 검증한다.
 * - POST /tasks/{taskId}/start       → 200 OK 검증
 * - POST /tasks/{taskId}/finish-draft → 200 OK 검증
 * - POST /tasks/{taskId}/finish       → 200 OK 검증
 * </p>
 */
@WithMockUser
@WebMvcTest(TaskCommandController.class)
class TaskCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskCommandService taskCommandService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/scm/tasks/1/start → 200 OK + ApiResponse.success")
    void startTask_Return200() throws Exception {
        // given
        doNothing().when(taskCommandService).startTask(anyLong());

        // when & then
        mockMvc.perform(post("/api/v1/scm/tasks/1/start")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/scm/tasks/1/finish-draft → 200 OK + ApiResponse.success")
    void finishDraft_Return200() throws Exception {
        // given
        TaskFinishRequest request = new TaskFinishRequest("임시 코멘트");
        doNothing().when(taskCommandService).finishDraft(anyLong(), any());

        // when & then
        mockMvc.perform(post("/api/v1/scm/tasks/1/finish-draft")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/scm/tasks/1/finish → 200 OK + ApiResponse.success")
    void finish_Return200() throws Exception {
        // given
        TaskFinishRequest request = new TaskFinishRequest("최종 코멘트");
        doNothing().when(taskCommandService).finish(anyLong(), any());

        // when & then
        mockMvc.perform(post("/api/v1/scm/tasks/1/finish")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
