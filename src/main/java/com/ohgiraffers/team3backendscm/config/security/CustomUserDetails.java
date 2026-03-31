package com.ohgiraffers.team3backendscm.config.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Spring Security 의 User 를 확장한 커스텀 인증 주체(Principal) 클래스.
 * 기본 username/password/authorities 외에 SCM 도메인에서 필요한 employee_id 를 추가로 보유한다.
 * AuditorAwareImpl 에서 이 클래스를 통해 현재 사용자의 employee_id 를 JPA Auditing 에 제공한다.
 */
@Getter
public class CustomUserDetails extends User {

    /** 시스템 전반에서 사용되는 직원 고유 식별자 */
    private final Long employeeId;

    /**
     * CustomUserDetails 생성자.
     *
     * @param employeeId  직원 고유 ID (SCM 도메인 내 created_by / updated_by 에 기록됨)
     * @param username    로그인 아이디
     * @param password    비밀번호 (암호화된 값)
     * @param authorities 부여된 권한 목록
     */
    public CustomUserDetails(Long employeeId, String username, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.employeeId = employeeId;
    }
}
