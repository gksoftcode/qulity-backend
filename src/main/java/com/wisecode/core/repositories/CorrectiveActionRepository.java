package com.wisecode.core.repositories;

import com.wisecode.core.entities.CorrectiveAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CorrectiveActionRepository extends JpaRepository<CorrectiveAction,Long> {
    @Query("select ca from CorrectiveAction ca where ca.auditItemId in (select cli.id from CheckListItem cli where cli.auditPlanId = :auditId)")
    Page<CorrectiveAction> findAllByAuditId(Long auditId, Pageable pageable);

    @Query("select ca from CorrectiveAction ca where ca.auditItemId in " +
            "(select cli.id from CheckListItem cli where cli.auditPlanId in " +
            "(select audit.id from AuditFinalReport afr join afr.auditPlans audit where afr.id = :auditReportId))")
    Page<CorrectiveAction> findAllByReportId(@Param("auditReportId") Long auditReportId, Pageable pageable);

    CorrectiveAction findByAuditItemId(Long auditItemId);
    @Modifying
    @Query("update CorrectiveAction  ca set ca.approved = :approved ," +
            " ca.approvedEmpId = :approvedEmpId , ca.approvedDate =:approvedDate where ca.id = :id")
    int updateApproved(@Param("approved") Integer approved,@Param("approvedEmpId") Long approvedEmpId ,@Param("approvedDate") Date approvedDate,@Param("id") Long id);
    @Modifying
    @Query("update CorrectiveAction  ca set ca.status = :status ," +
            " ca.followEmpId = :followEmpId , ca.followDate =:followDate , ca.followDescription = :remarks where ca.id = :id")
    int updateFollow(@Param("status") Integer status, @Param("followEmpId") Long followEmpId ,
                     @Param("followDate") Date followDate, @Param("id") Long id,
                     @Param("remarks") String remarks);
}
