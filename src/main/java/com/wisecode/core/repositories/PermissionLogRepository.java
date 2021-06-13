package com.wisecode.core.repositories;

import com.wisecode.core.entities.PermissionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionLogRepository extends JpaRepository<PermissionLog,Long> {
}
