package com.ohgiraffers.team3backendscm.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * JWT 에서 추출한 사원코드(employeeCode)로 DB 를 조회해 {@link CustomUserDetails} 를 생성하는 서비스.
 *
 * <p>Admin 모듈이 발행한 JWT 의 {@code sub} 클레임은 {@code employee_code}(사원코드, String)이다.
 * SCM 도메인 서비스는 {@code employee_id}(Long, DB PK)를 사용하므로,
 * 이 서비스가 두 값을 연결하는 역할을 한다.</p>
 *
 * <p>조회 대상 컬럼: {@code employee_id}, {@code employee_role} (employee 테이블)</p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 사원코드로 employee 테이블을 조회해 {@link CustomUserDetails}를 반환한다.
     *
     * @param employeeCode JWT sub 클레임 값 (사원코드)
     * @return employeeId, employeeCode, role 이 채워진 CustomUserDetails
     * @throws UsernameNotFoundException 해당 사원코드가 존재하지 않을 때
     */
    @Override
    public UserDetails loadUserByUsername(String employeeCode) throws UsernameNotFoundException {
        String sql = "SELECT employee_id, employee_role FROM employee WHERE employee_code = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, employeeCode);

        if (rows.isEmpty()) {
            throw new UsernameNotFoundException("존재하지 않는 사원코드입니다: " + employeeCode);
        }

        Map<String, Object> row = rows.get(0);
        Long   employeeId   = ((Number) row.get("employee_id")).longValue();
        String employeeRole = (String) row.get("employee_role");

        String authority = employeeRole.startsWith("ROLE_") ? employeeRole : "ROLE_" + employeeRole;

        return new CustomUserDetails(
                employeeId,
                employeeCode,
                "",   // JWT 인증이므로 password 불필요
                List.of(new SimpleGrantedAuthority(authority))
        );
    }
}
