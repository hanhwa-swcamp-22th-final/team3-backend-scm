package com.ohgiraffers.team3backendscm.scm.command.application.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.team3backendscm.common.exception.GlobalExceptionHandler;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.admin.OrderCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderCommandControllerTest {

    @Mock
    private OrderCommandService orderCommandService;

    @InjectMocks
    private OrderCommandController orderCommandController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderCommandController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    @DisplayName("POST /orders - 성공 시 201 CREATED 반환")
    void createOrder_Success() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest(1L, 2L, "ORD-001", 100, LocalDate.now(), true);
        given(orderCommandService.create(any(OrderCreateRequest.class))).willReturn(10L);

        mockMvc.perform(post("/api/v1/scm/admin/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(10L));
    }

    @Test
    @DisplayName("PUT /orders/{id} - 성공 시 200 OK 반환")
    void updateOrder_Success() throws Exception {
        OrderUpdateRequest request = new OrderUpdateRequest(1L, "ORD-002", 200, LocalDate.now());

        mockMvc.perform(put("/api/v1/scm/admin/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("PUT /orders/{id} - 주문이 없으면 404 NOT FOUND 반환")
    void updateOrder_NotFound() throws Exception {
        OrderUpdateRequest request = new OrderUpdateRequest(1L, "ORD-002", 200, LocalDate.now());
        willThrow(new NoSuchElementException("Not Found")).given(orderCommandService).update(anyLong(), any(OrderUpdateRequest.class));

        mockMvc.perform(put("/api/v1/scm/admin/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /orders/{id} - 성공 시 200 OK 반환")
    void deleteOrder_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/scm/admin/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("DELETE /orders/{id} - 주문 상태가 REGISTERED가 아니면 400 BAD REQUEST 반환")
    void deleteOrder_Fail_WhenNotRegistered() throws Exception {
        willThrow(new IllegalStateException("REGISTERED 상태가 아닙니다")).given(orderCommandService).delete(anyLong());

        mockMvc.perform(delete("/api/v1/scm/admin/orders/1"))
                .andExpect(status().isBadRequest());
    }
}
