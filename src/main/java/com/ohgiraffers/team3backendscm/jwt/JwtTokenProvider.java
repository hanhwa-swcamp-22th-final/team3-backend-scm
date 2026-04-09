package com.ohgiraffers.team3backendscm.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰의 생성, 파싱, 검증을 담당하는 컴포넌트.
 * Admin 모듈에서 사용하는 비밀키와 동일한 키를 사용하여 토큰을 검증한다.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 토큰의 유효성을 검증한다.
     *
     * @param token 검증할 JWT 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("유효하지 않은 JWT 토큰입니다.");
        } catch (ExpiredJwtException ex) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException ex) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException ex) {
            log.error("JWT 토큰이 비어있습니다.");
        }
        return false;
    }

    /**
     * JWT 토큰에서 사용자 식별자(사원코드)를 추출한다.
     * Admin 모듈에서는 sub 클레임에 employee_code를 담는다.
     *
     * @param token JWT 토큰
     * @return 사원코드 (employeeCode)
     */
    public String getEmployeeCode(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * JWT 토큰에서 권한(Role) 정보를 추출한다.
     *
     * @param token JWT 토큰
     * @return 권한 문자열 (예: "TL", "WORKER")
     */
    public String getRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }
}
