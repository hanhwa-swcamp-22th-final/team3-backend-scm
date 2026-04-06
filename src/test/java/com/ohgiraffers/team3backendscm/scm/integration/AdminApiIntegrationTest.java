package com.ohgiraffers.team3backendscm.scm.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ProductUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Product;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Admin 호출 내부 API 통합 테스트")
class AdminApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private IdGenerator idGenerator;
    @PersistenceContext private EntityManager entityManager;

    @Nested
    @DisplayName("제품(Product) 동기화 API")
    class ProductApiTest {

        @Test
        @DisplayName("제품 등록 성공 시 201 응답 및 DB 저장 확인")
        void createProduct() throws Exception {
            ProductCreateRequest request = new ProductCreateRequest("신규 제품", "P-NEW");

            mockMvc.perform(post("/api/v1/scm/admin/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));

            // 검증
            boolean exists = productRepository.findAll().stream()
                    .anyMatch(p -> p.getProductCode().equals("P-NEW"));
            assertTrue(exists);
        }

        @Test
        @DisplayName("제품 수정 성공 시 200 응답 및 DB 갱신 확인")
        void updateProduct() throws Exception {
            Long productId = idGenerator.generate();
            Product product = Product.create(productId, "구 제품명", "P-OLD");
            productRepository.save(product);

            ProductUpdateRequest request = new ProductUpdateRequest("수정 제품명", "P-UPDATED");

            mockMvc.perform(put("/api/v1/scm/admin/products/" + productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            entityManager.flush();
            entityManager.clear();

            Product updated = productRepository.findById(productId).orElseThrow();
            assertEquals("수정 제품명", updated.getProductName());
            assertEquals("P-UPDATED", updated.getProductCode());
        }

        @Test
        @DisplayName("제품 삭제 성공 시 200 응답 및 DB 삭제 확인")
        void deleteProduct() throws Exception {
            Long productId = idGenerator.generate();
            Product product = Product.create(productId, "삭제할 제품", "P-DEL");
            productRepository.save(product);

            mockMvc.perform(delete("/api/v1/scm/admin/products/" + productId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            assertFalse(productRepository.existsById(productId));
        }
    }

    @Nested
    @DisplayName("주문(Order) 동기화 API")
    class OrderApiTest {

        @Test
        @DisplayName("주문 등록 성공 시 201 응답 및 DB 저장 확인")
        void createOrder() throws Exception {
            Long productId = idGenerator.generate();
            Long configId = idGenerator.generate();
            
            // 테스트용 의존 데이터 삽입
            jdbcTemplate.update(
                    "INSERT INTO product (product_id, product_name, product_code) VALUES (?, ?, ?)",
                    productId, "테스트 제품", "TEST-PROD");
            jdbcTemplate.update(
                    "INSERT INTO OCSA_weight_config (config_id, industry_preset) VALUES (?, ?)",
                    configId, "SEMICONDUCTOR");

            OrderCreateRequest request = new OrderCreateRequest(productId, configId, "ORD-NEW", 50, LocalDate.now().plusDays(5), true);

            mockMvc.perform(post("/api/v1/scm/admin/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));

            // 검증
            boolean exists = orderRepository.findAll().stream()
                    .anyMatch(o -> o.getOrderNumber().equals("ORD-NEW") && o.getStatus() == OrderStatus.REGISTERED);
            assertTrue(exists);
        }

        @Test
        @DisplayName("주문 수정 성공 시 200 응답 및 DB 갱신 확인 (REGISTERED 상태)")
        void updateOrder() throws Exception {
            Long productId = idGenerator.generate();
            Long configId = idGenerator.generate();
            
            jdbcTemplate.update(
                    "INSERT INTO product (product_id, product_name, product_code) VALUES (?, ?, ?)",
                    productId, "테스트 제품", "TEST-PROD");
            jdbcTemplate.update(
                    "INSERT INTO OCSA_weight_config (config_id, industry_preset) VALUES (?, ?)",
                    configId, "SEMICONDUCTOR");

            Long orderId = idGenerator.generate();
            Order order = Order.register(orderId, productId, configId, "ORD-OLD", 10, LocalDate.now().plusDays(5), true);
            orderRepository.save(order);

            OrderUpdateRequest request = new OrderUpdateRequest(productId, "ORD-UPDATED", 20, LocalDate.now().plusDays(2));

            mockMvc.perform(put("/api/v1/scm/admin/orders/" + orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            entityManager.flush();
            entityManager.clear();

            Order updated = orderRepository.findById(orderId).orElseThrow();
            assertEquals("ORD-UPDATED", updated.getOrderNumber());
            assertEquals(20, updated.getOrderQuantity());
        }

        @Test
        @DisplayName("주문 삭제 성공 시 200 응답 및 DB 삭제 확인")
        void deleteOrder() throws Exception {
            Long productId = idGenerator.generate();
            Long configId = idGenerator.generate();
            
            jdbcTemplate.update(
                    "INSERT INTO product (product_id, product_name, product_code) VALUES (?, ?, ?)",
                    productId, "테스트 제품", "TEST-PROD");
            jdbcTemplate.update(
                    "INSERT INTO OCSA_weight_config (config_id, industry_preset) VALUES (?, ?)",
                    configId, "SEMICONDUCTOR");

            Long orderId = idGenerator.generate();
            Order order = Order.register(orderId, productId, configId, "ORD-DEL", 10, LocalDate.now().plusDays(5), true);
            orderRepository.save(order);

            mockMvc.perform(delete("/api/v1/scm/admin/orders/" + orderId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            assertFalse(orderRepository.existsById(orderId));
        }

        @Test
        @DisplayName("REGISTERED 상태가 아닌 주문 수정/삭제 시 400 반환")
        void updateAndDelete_Fail_WhenNotRegistered() throws Exception {
            Long productId = idGenerator.generate();
            Long configId = idGenerator.generate();
            
            jdbcTemplate.update(
                    "INSERT INTO product (product_id, product_name, product_code) VALUES (?, ?, ?)",
                    productId, "테스트 제품", "TEST-PROD");
            jdbcTemplate.update(
                    "INSERT INTO OCSA_weight_config (config_id, industry_preset) VALUES (?, ?)",
                    configId, "SEMICONDUCTOR");

            Long orderId = idGenerator.generate();
            // 강제로 ANALYZED 상태로 DB 삽입
            jdbcTemplate.update(
                    "INSERT INTO orders (order_id, product_id, config_id, order_no, order_quantity, order_status, order_deadline) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    orderId, productId, configId, "ORD-ANALYZED", 1, "ANALYZED", LocalDate.now().plusDays(5).toString());

            // 1. 수정 시도
            OrderUpdateRequest request = new OrderUpdateRequest(productId, "ORD-FAIL", 20, LocalDate.now().plusDays(2));
            mockMvc.perform(put("/api/v1/scm/admin/orders/" + orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            // 2. 삭제 시도
            mockMvc.perform(delete("/api/v1/scm/admin/orders/" + orderId))
                    .andExpect(status().isBadRequest());
        }
    }
}