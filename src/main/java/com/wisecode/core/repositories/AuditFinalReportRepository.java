package com.wisecode.core.repositories;

import com.wisecode.core.entities.AuditFinalReport;
import com.wisecode.core.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditFinalReportRepository extends JpaRepository<AuditFinalReport,Long> {
    @Query("select afr from AuditFinalReport afr where afr.title like %:title% and (:yearId = -1 or afr.yearId = :yearId) and (:status=-1 or afr.status = :status)")
    Page<AuditFinalReport> search(@Param("title") String title, @Param("yearId") Integer yearId, @Param("status")Integer status, Pageable page);

    @Query("select distinct audit.auditors from AuditFinalReport afr join afr.auditPlans audit where afr.id = :reportId")
    List<Employee> employeesByReportId(@Param("reportId")Long reportId);

    @Modifying
    @Query("update AuditFinalReport afr set afr.status =:status where afr.id= :id")
    int updateStatus(@Param("id") Long id,@Param("status") Integer status);
}
