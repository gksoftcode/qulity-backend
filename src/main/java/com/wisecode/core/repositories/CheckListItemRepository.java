package com.wisecode.core.repositories;

import com.wisecode.core.entities.CheckListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckListItemRepository extends JpaRepository<CheckListItem, Long> {

    List<CheckListItem> findByAuditPlanIdOrderById(Long audit_plan_id);
}
