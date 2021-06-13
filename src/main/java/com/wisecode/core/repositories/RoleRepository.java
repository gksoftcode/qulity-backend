package com.wisecode.core.repositories;

import com.wisecode.core.RoleName;
import com.wisecode.core.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(RoleName roleName);
}
