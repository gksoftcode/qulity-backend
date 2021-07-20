package com.wisecode.core.repositories;

import com.wisecode.core.entities.AuditPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditPlanRepository extends JpaRepository<AuditPlan,Long> {

    @Query("select audit from AuditPlan audit where (:yearId is null or YEAR(audit.fromDate) = :yearId)")
    Page<AuditPlan> search(@Param("yearId") Integer yearId, Pageable pageable);

    @Modifying
    @Query("update AuditPlan ap set ap.status =:status where ap.id= :id")
    int updateStatus(@Param("id") Long id,@Param("status") Integer status);
}
