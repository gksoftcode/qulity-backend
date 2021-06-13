package com.wisecode.core.repositories;

import com.wisecode.core.entities.AuditPlanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditPlanTransactionRepository extends JpaRepository<AuditPlanTransaction,Long> {
}
