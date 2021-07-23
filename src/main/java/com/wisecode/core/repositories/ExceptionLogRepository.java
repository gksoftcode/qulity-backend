package com.wisecode.core.repositories;

import com.wisecode.core.entities.ExceptionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExceptionLogRepository extends JpaRepository<ExceptionLog, Long> {

}
