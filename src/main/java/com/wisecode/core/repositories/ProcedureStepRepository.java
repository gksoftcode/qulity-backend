package com.wisecode.core.repositories;

import com.wisecode.core.entities.ProcedureStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProcedureStepRepository extends JpaRepository<ProcedureStep,Long> {

    @Query("select count(step) from ProcedureStep step join step.files atta where atta.id = :id")
    Integer countAttachmentUsed(Long id);
}
