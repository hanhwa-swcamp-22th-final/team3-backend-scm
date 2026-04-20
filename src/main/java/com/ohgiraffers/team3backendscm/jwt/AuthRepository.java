package com.ohgiraffers.team3backendscm.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<RefreshToken, String> {
}
