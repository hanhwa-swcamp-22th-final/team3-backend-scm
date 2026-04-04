package com.ohgiraffers.team3backendscm.scm.command.infrastructure.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<Product, Long> {
}
