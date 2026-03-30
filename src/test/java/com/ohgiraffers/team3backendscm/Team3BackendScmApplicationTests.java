package com.ohgiraffers.team3backendscm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring Boot 애플리케이션 컨텍스트 로딩 테스트.
 *
 * <p>테스트 전략: 통합 테스트 (@SpringBootTest)
 * - 애플리케이션 전체 컨텍스트가 정상적으로 구성되는지 확인한다.
 * - 빈 등록, 설정 파일 로딩, 데이터소스 연결 등 초기화 오류를 조기에 검출한다.
 * </p>
 */
@SpringBootTest
class Team3BackendScmApplicationTests {

    @Test
    void contextLoads() {
        // 애플리케이션 컨텍스트가 예외 없이 로딩되면 테스트 통과
    }

}
