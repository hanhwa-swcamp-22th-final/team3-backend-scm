package com.ohgiraffers.team3backendscm.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 데이터베이스 연결 상태 확인 테스트.
 *
 * <p>테스트 전략: 통합 테스트 (@SpringBootTest)
 * - 실제 DataSource를 주입받아 DB 연결이 유효한지 검증한다.
 * - 연결 대상 스키마(setodb), URL, 사용자명, DB 제품명을 콘솔에 출력하여
 *   환경 설정 이상을 빠르게 파악할 수 있도록 한다.
 * </p>
 */
@SpringBootTest
class DatabaseConnectionTest {

    // Spring이 관리하는 DataSource 빈 주입
    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("DB 연결 테스트")
    void testDatabaseConnection() throws Exception {
        // given - DataSource 빈이 정상 주입되었는지 확인
        assertNotNull(dataSource, "DataSource should not be null");

        // when - 실제 DB 커넥션 획득 시도
        try (Connection connection = dataSource.getConnection()) {

            // then - 커넥션이 유효하게 열려 있는지 확인
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");

            // 연결된 DB 메타데이터를 콘솔에 출력 (디버깅 및 환경 확인 용도)
            String catalog = connection.getCatalog();
            System.out.println("Connected to database: " + catalog);
            System.out.println("DB URL: " + connection.getMetaData().getURL());
            System.out.println("DB User: " + connection.getMetaData().getUserName());
            System.out.println("DB Product: " + connection.getMetaData().getDatabaseProductName()
                    + " " + connection.getMetaData().getDatabaseProductVersion());

            // 연결된 스키마가 'setodb' 인지 검증
            assertEquals("setodb", catalog, "Should be connected to setodb schema");
        }
    }
}
