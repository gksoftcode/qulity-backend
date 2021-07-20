package com.wisecode.core.repositories;

import com.wisecode.core.entities.CorrectiveAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Date;

public interface CorrectiveActionRepository extends JpaRepository<CorrectiveAction,Long> {
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
