package com.ohgiraffers.team3backendscm.scm.command.domain.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Order 엔티티에 대한 JPA 리포지토리 인터페이스.
 * 기본 CRUD 및 페이징/정렬 기능을 JpaRepository 에서 상속받는다.
 * Command 레이어에서 주문 저장·조회·수정에 사용된다.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
