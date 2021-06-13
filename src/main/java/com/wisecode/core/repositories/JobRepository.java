package com.wisecode.core.repositories;

import com.wisecode.core.entities.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job,Long> {

    Page<Job> findAllByNameIsLike(String name, Pageable pageable);
}
