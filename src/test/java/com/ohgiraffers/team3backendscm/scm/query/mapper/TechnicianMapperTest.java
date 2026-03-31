package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.TechnicianDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * TechnicianMapper MyBatis 쿼리 통합 테스트.
 *
 * <p>테스트 전략: @SpringBootTest — 실제 DB에 대해 SQL 바인딩·ResultMap 매핑 오류를 검증한다.
 * </p>
 */
@SpringBootTest
@Transactional
@DisplayName("TechnicianMapper XML 쿼리 테스트")
class TechnicianMapperTest {

    @Autowired
    private TechnicianMapper technicianMapper;

    @Test
    @DisplayName("SQL 실행 및 ResultMap 매핑이 오류 없이 완료된다")
    void findTechnicians_ExecutesWithoutError() {
        List<TechnicianDto> result = technicianMapper.findTechnicians();

        assertNotNull(result, "결과 리스트가 null 이면 안 된다");
    }

    @Test
    @DisplayName("데이터가 있으면 employeeId·tier 필드가 매핑된다")
    void findTechnicians_MapsFields_WhenDataExists() {
        List<TechnicianDto> result = technicianMapper.findTechnicians();
        assumeTrue(!result.isEmpty(), "기술자 데이터 없음 — skip");

        TechnicianDto first = result.get(0);
        assertNotNull(first.getEmployeeId(), "employeeId 매핑 확인");
        assertNotNull(first.getTier(),       "tier 매핑 확인");
    }
}
