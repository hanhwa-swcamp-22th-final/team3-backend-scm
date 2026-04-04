package com.ohgiraffers.team3backendscm.scm.command.application.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.team3backendscm.common.exception.GlobalExceptionHandler;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.admin.ProductCommandService;
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

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductCommandControllerTest {

    @Mock
    private ProductCommandService productCommandService;

    @InjectMocks
    private ProductCommandController productCommandController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productCommandController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /products - 성공 시 201 CREATED 반환")
    void createProduct_Success() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest("제품A", "PROD-A");
        given(productCommandService.create(any(ProductCreateRequest.class))).willReturn(1L);

        mockMvc.perform(post("/api/v1/scm/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(1L));
    }

    @Test
    @DisplayName("PUT /products/{id} - 성공 시 200 OK 반환")
    void updateProduct_Success() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest("제품B", "PROD-B");

        mockMvc.perform(put("/api/v1/scm/admin/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("PUT /products/{id} - 제품이 없으면 404 NOT FOUND 반환")
    void updateProduct_NotFound() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest("제품B", "PROD-B");
        willThrow(new NoSuchElementException("Not Found")).given(productCommandService).update(anyLong(), any(ProductUpdateRequest.class));

        mockMvc.perform(put("/api/v1/scm/admin/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /products/{id} - 성공 시 200 OK 반환")
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/scm/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /products - 제품명이 빈 값이면 400 BAD REQUEST 반환")
    void createProduct_Fail_WhenBlankName() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest("", "PROD-A");

        mockMvc.perform(post("/api/v1/scm/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /products - 제품 코드가 빈 값이면 400 BAD REQUEST 반환")
    void createProduct_Fail_WhenBlankCode() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest("제품A", "");

        mockMvc.perform(post("/api/v1/scm/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /products/{id} - 제품이 없으면 404 NOT FOUND 반환")
    void deleteProduct_Fail_WhenNotFound() throws Exception {
        willThrow(new NoSuchElementException("Not Found")).given(productCommandService).delete(anyLong());

        mockMvc.perform(delete("/api/v1/scm/admin/products/99"))
                .andExpect(status().isNotFound());
    }
}
