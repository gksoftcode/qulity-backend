package com.wisecode.core.repositories;

import com.wisecode.core.entities.WorkGuideProcedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkGuideProcedureRepository extends JpaRepository<WorkGuideProcedure,Long> {

}
