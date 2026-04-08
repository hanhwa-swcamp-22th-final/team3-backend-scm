package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.TechnicianDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 기술자(Technician) 조회 전용 MyBatis 매퍼 인터페이스.
 * 팀 리더가 배정 가능한 기술자 목록을 조회할 때 사용한다.
 * SQL은 src/main/resources/mappers/ 에 정의된다.
 */
@Mapper
public interface TechnicianMapper {

    /**
     * 배정 가능한 전체 기술자 목록을 조회한다.
     * 각 기술자의 이름, 역량 티어, OCSA 점수, 적합도를 포함한다.
     *
     * @return 기술자 목록
     */
    List<TechnicianDto> findTechnicians();
}
