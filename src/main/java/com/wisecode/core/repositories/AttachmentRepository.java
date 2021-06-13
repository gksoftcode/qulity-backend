package com.wisecode.core.repositories;

import com.wisecode.core.entities.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Attachment findByUuid(String uuid);
    List<Attachment> findByDepartmentIdOrderByOrderNo(Long departmentId);

    @Query("select atta from Attachment atta where (:status = -1 or atta.status = :status) and" +
            " (:all_department = 1 or atta.departmentId in (:department_list) ) ")
    Page<Attachment> search(@Param("department_list")List<Long> department_list,
                                                               @Param("status") Integer status,
                                                               @Param("all_department")Integer all_department,
                                                               Pageable pageable);

    List<Attachment> findByDepartmentIdAndStatusOrderByOrderNo(Long departmentId, Integer status);

    @Query("select count(stp) from ProcedureStep stp inner join stp.files file where file = :at ")
    Long countForDelete(@Param("at") Attachment at);

    @Transactional
    @Modifying
    @Query("update Attachment atta set atta.status = :nextStatus " +
            "where atta.id = :id")
    Integer updateStatus(@Param("id") Long id,@Param("nextStatus") Integer nextStatus);
}
