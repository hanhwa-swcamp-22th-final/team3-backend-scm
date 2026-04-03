package com.ohgiraffers.team3backendscm.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * JWT 토큰 검증 및 클레임 추출 유틸리티.
 * 토큰 발급은 Admin 모듈에서 수행하며, 이 클래스는 검증과 파싱만 담당한다.
 *
 * <p>Admin 모듈이 생성하는 JWT 페이로드 구조:
 * <pre>
 * {
 *   "sub":            "사원코드 (employeeCode)",
 *   "employeeId":     1234,          // Long — Admin 측에서 추가 필요
 *   "role":           "TL",
 *   "employeeName":   "홍길동",
 *   "departmentName": "생산부",
 *   "teamName":       "1팀"
 * }
 * </pre>
 * ⚠ employeeId 클레임은 Admin 모듈의 JwtTokenProvider.createToken() 에
 *   .claim("employeeId", employeeId) 를 추가해야 사용 가능하다.
 * </p>
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 토큰의 서명·만료 여부를 검증한다.
     * 만료된 토큰도 false 를 반환한다 (예외를 외부로 전파하지 않는다).
     *
     * @param token Bearer 이후의 순수 토큰 문자열
     * @return 유효하면 {@code true}, 그 외 {@code false}
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false; // 만료 토큰
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 서명 불일치·형식 오류
        }
    }

    /**
     * 토큰에서 사원코드({@code sub})를 반환한다.
     *
     * @param token 유효한 토큰
     * @return 사원코드 (로그인 아이디)
     */
    public String getEmployeeCode(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰에서 직원 고유 ID({@code employeeId})를 반환한다.
     * Admin 모듈에서 해당 클레임을 포함하지 않으면 {@code null}이 반환된다.
     *
     * @param token 유효한 토큰
     * @return 직원 고유 ID (Long), 클레임 없으면 null
     */
    public Long getEmployeeId(String token) {
        return parseClaims(token).get("employeeId", Long.class);
    }

    /**
     * 토큰에서 권한({@code role})을 반환한다.
     *
     * @param token 유효한 토큰
     * @return 권한 문자열 (예: "TL", "WORKER")
     */
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
