package com.ohgiraffers.team3backendscm.scm.query.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * HR 도메인 employee 테이블에 대한 읽기 전용 MyBatis 매퍼.
 * SCM 도메인이 배정 처리 시 기술자의 역량 티어를 조회하는 데 사용한다.
 * SQL은 src/main/resources/mappers/employees.xml 에 정의된다.
 */
@Mapper
public interface EmployeeMapper {

    /**
     * 직원 ID로 역량 티어를 조회한다.
     *
     * @param employeeId 조회할 직원 ID
     * @return 역량 티어 문자열 (S / A / B / C), 존재하지 않으면 null
     */
    String findTierById(Long employeeId);
}
