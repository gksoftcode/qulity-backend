package com.wisecode.core.repositories;

import com.wisecode.core.dto.WorkGuideDto;
import com.wisecode.core.dto.WorkGuideDtoInterface;
import com.wisecode.core.entities.WorkGuide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkGuideRepository extends JpaRepository<WorkGuide, Long> {
    WorkGuide findByDepartmentIdAndType(Long departmentId,Integer type);
    @Query("select wg from WorkGuide wg where (:status_id = -1 or wg.status = :status_id) " +
            "and (:type_id = -1 or wg.type = :type_id)  " +
            "and (:department_list is null or wg.department.id in (:department_list))")
    Page<WorkGuide> search(@Param("type_id")Integer type_id, @Param("status_id") Integer status_id,
                           @Param("department_list")List<Long> department_list, Pageable pageable);

    @Query(value = "select new com.wisecode.core.dto.WorkGuideDto(wg.id,wg.status,wg.type,wg.departmentId," +
            " wg.version,(select name from Department  where id = wg.departmentId )," +
            "(select departmentNo from Department  where id = wg.departmentId )," +
            "(select level from Department  where id = wg.departmentId ) ," +
            " (select max(createdAt) from WorkGuideTransaction where workGuideId = wg.id) )  " +
            "from WorkGuide wg where (:status_id = -1 or wg.status = :status_id) " +
           "and (:type_id = -1 or wg.type = :type_id)  " +
            "and (:all_department = 1 or wg.department.id in ( :department_list ))",
            countQuery = "select count (wg) from WorkGuide wg where (:status_id = -1 or wg.status = :status_id) " +
                    "and (:type_id = -1 or wg.type = :type_id) " +
                    "and (:all_department = 1 or wg.department.id in ( :department_list ))")
    Page<WorkGuideDto> search2(@Param("type_id")Integer type_id, @Param("status_id") Integer status_id,
                               @Param("department_list")List<Long> department_list,
                               @Param("all_department")Integer all_department, Pageable pageable);

    @Query(value ="select tt.* from (select wg.id as id,wg.status as status,wg.work_guide_type as type ,wg.department_id as departmentId,wg.version as version " +
            " ,dep.name as departmentName , dep.department_no as departmentNo , dep.level level ," +
            " (select max(created_at) from work_guide_transaction  where work_guide_id = wg.id) as lastActionDate " +
            " from work_guide wg inner join department dep on wg.department_id = dep.id where (:status_id = -1 or wg.status = :status_id) " +
            "and (:type_id = -1 or wg.work_guide_type = :type_id)  " +
            "and (:department_list is null or dep.id in (:department_list)))tt",
            nativeQuery = true,
            countQuery =" select  count(*)  from work_guide wg inner join department dep on wg.department_id = dep.id where (:status_id = -1 or wg.status = :status_id) " +
            "and (:type_id = -1 or wg.work_guide_type = :type_id)  " +
            "and (:department_list is null or dep.id in (:department_list))")
    Page<WorkGuideDtoInterface> search4(@Param("type_id")Integer type_id, @Param("status_id") Integer status_id,
                                        @Param("department_list")List<Long> department_list, Pageable pageable);

    @Query(name = "search123",
            nativeQuery = true,
            countQuery =" select  count(*)  from work_guide wg inner join department dep on wg.department_id = dep.id where (:status_id = -1 or wg.status = :status_id) " +
                    "and (:type_id = -1 or wg.work_guide_type = :type_id)  " +
                    "and (:department_list is null or dep.id in (:department_list))")
    Page<WorkGuideDto> search3(@Param("type_id")Integer type_id, @Param("status_id") Integer status_id,
                               @Param("department_list")List<Long> department_list, Pageable pageable);

    @Query("select wg from WorkGuide wg where wg.departmentId = :departmentId " +
            "and wg.type = com.wisecode.core.controller.WorkGuideController.TYPE_PRODUCTION")
    WorkGuide hasProduction(@Param("departmentId") Long departmentId);

    @Query("select count(wg) from WorkGuide wg where wg.departmentId = :departmentId " +
            "and (wg.type = com.wisecode.core.controller.WorkGuideController.TYPE_NEW " +
            "or wg.type = com.wisecode.core.controller.WorkGuideController.TYPE_NEW_VERSION)")
    int hasDuplicateOrNew(@Param("departmentId") Long departmentId);

    @Modifying
    @Query("update WorkGuide wg set wg.status =:status where wg.id= :id")
    int updateStatus(@Param("id") Long id,@Param("status") Integer status);



    @Modifying
    @Query("update WorkGuide wg set wg.type = com.wisecode.core.controller.WorkGuideController.TYPE_VERSIONED" +
            " where wg.id <> :id and wg.departmentId = :departmentId " +
            "and wg.type = com.wisecode.core.controller.WorkGuideController.TYPE_PRODUCTION")
    int updateOldType(@Param("id") Long id,@Param("departmentId") Long departmentId);
}
