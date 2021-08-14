package com.wisecode.core.repositories;

import com.wisecode.core.entities.AuditPlan;
import com.wisecode.core.entities.Employee;
import org.apache.catalina.LifecycleState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditPlanRepository extends JpaRepository<AuditPlan,Long> {

    @Query("select audit from AuditPlan audit  left join audit.auditors auditor  where ((auditor.id = :auditor) " +
            " or ( audit.departmentId in (:depId)))" +
            " and (:yearId is null or YEAR(audit.fromDate) = :yearId)")
    Page<AuditPlan> search(@Param("yearId") Integer yearId,@Param("auditor") Long auditor,
                           @Param("depId") List<Long> depId,  Pageable pageable);

    @Query("select audit from AuditPlan audit  where  (:yearId is null or YEAR(audit.fromDate) = :yearId) " +
            " and (:fullSearch = 1 or audit.departmentId in (:depId))")
    Page<AuditPlan> searchAdmin(@Param("yearId") Integer yearId,@Param("fullSearch") Integer fullSearch, @Param("depId") List<Long> depId , Pageable pageable);


    @Modifying
    @Query("update AuditPlan ap set ap.status =:status where ap.id= :id")
    int updateStatus(@Param("id") Long id,@Param("status") Integer status);
}
