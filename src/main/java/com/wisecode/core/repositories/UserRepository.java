package com.wisecode.core.repositories;

import com.wisecode.core.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByIdIn(List<Long> userIds);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    @Modifying
    @Query("update User usr set usr.password = :password where usr.id = :id")
    Integer updatePassword(@Param("password") String password, @Param("id") Long id);
}
