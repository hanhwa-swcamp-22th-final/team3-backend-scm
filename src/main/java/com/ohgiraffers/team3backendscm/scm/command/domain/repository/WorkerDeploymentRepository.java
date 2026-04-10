package com.ohgiraffers.team3backendscm.scm.command.domain.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.WorkerDeployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WorkerDeploymentRepository extends JpaRepository<WorkerDeployment, Long> {

    /**
     * 특정 설비들에 현재 활성 배치된 기술자 ID 목록을 조회한다.
     * (end_date IS NULL OR end_date >= 오늘)
     */
    @Query("""
            SELECT wd.employeeId
            FROM WorkerDeployment wd
            WHERE wd.equipmentId IN :equipmentIds
              AND (wd.endDate IS NULL OR wd.endDate >= :today)
            """)
    List<Long> findActiveEmployeeIdsByEquipmentIds(
            @Param("equipmentIds") List<Long> equipmentIds,
            @Param("today") LocalDate today
    );

    /**
     * 특정 설비들에 활성 배치된 배치 레코드 수를 조회한다. (기술자 수 집계용)
     */
    @Query("""
            SELECT COUNT(DISTINCT wd.employeeId)
            FROM WorkerDeployment wd
            WHERE wd.equipmentId IN :equipmentIds
              AND (wd.endDate IS NULL OR wd.endDate >= :today)
            """)
    long countActiveEmployeesByEquipmentIds(
            @Param("equipmentIds") List<Long> equipmentIds,
            @Param("today") LocalDate today
    );
}
