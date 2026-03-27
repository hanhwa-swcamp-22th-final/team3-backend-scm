package com.ohgiraffers.team3backendscm.scm.query.controller;

import com.ohgiraffers.team3backendscm.scm.query.dto.OrderQueryRequest;
import com.ohgiraffers.team3backendscm.scm.query.service.OrderQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderQueryController.class)
class OrderQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderQueryService orderQueryService;

    @Test
    @DisplayName("GET /api/v1/scm/orders → 200 OK")
    void getOrders_Return200() throws Exception {
        given(orderQueryService.getOrders(any(OrderQueryRequest.class))).willReturn(List.of());

        mockMvc.perform(get("/api/v1/scm/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
