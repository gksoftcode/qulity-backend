package com.wisecode.core.repositories;

import com.wisecode.core.entities.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTypeRepository extends JpaRepository<DocumentType,Long> {

    Page<DocumentType> findAllByNameIsLike(String name, Pageable pageable);
}
