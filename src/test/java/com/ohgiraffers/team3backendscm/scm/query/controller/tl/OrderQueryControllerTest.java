package com.ohgiraffers.team3backendscm.scm.query.controller.tl;

import com.ohgiraffers.team3backendscm.scm.query.controller.tl.OrderQueryController;
import com.ohgiraffers.team3backendscm.scm.query.dto.request.OrderQueryRequest;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderOcsaDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.service.tl.OrderQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
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

    @Test
    @DisplayName("GET /api/v1/scm/orders/urgent → 200 OK")
    void getUrgentOrders_Return200() throws Exception {
        // given
        given(orderQueryService.getUrgentOrders()).willReturn(List.of());

        // when / then
        mockMvc.perform(get("/api/v1/scm/orders/urgent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/orders/1 → 200 OK")
    void getOrder_Return200() throws Exception {
        // given
        given(orderQueryService.getOrderById(anyLong())).willReturn(new OrderDetailDto());

        // when / then
        mockMvc.perform(get("/api/v1/scm/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/orders/summary → 200 OK")
    void getOrderSummary_Return200() throws Exception {
        // given
        given(orderQueryService.getOrderSummary()).willReturn(new OrderSummaryDto());

        // when / then
        mockMvc.perform(get("/api/v1/scm/orders/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/scm/orders/1/ocsa → 200 OK")
    void getOrderOcsa_Return200() throws Exception {
        // given
        given(orderQueryService.getOrderOcsa(anyLong())).willReturn(new OrderOcsaDto());

        // when / then
        mockMvc.perform(get("/api/v1/scm/orders/1/ocsa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
