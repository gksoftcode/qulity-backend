package com.wisecode.core.repositories;

import com.wisecode.core.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Department findDepartmentByParentDepartmentIsNull();

    @Modifying
    @Query("update Department dep set dep.managerId = :manager_id where dep.id = :id ")
    Integer setManager(@Param("manager_id") Long manager_id,@Param("id") Long id);

    @Modifying
    @Query("update Department dep set dep.orderNo = :order_no where dep.id = :id ")
    Integer setOrderNo(@Param("id") Long id,@Param("order_no") Integer order_no);

    @Query(value="SELECT  DATA.*,id.lvl FROM( " +
            " SELECT @ids as _ids," +
            "( SELECT @ids \\:= GROUP_CONCAT(id) " +
            " FROM department " +
            " WHERE FIND_IN_SET(parent_department_id, @ids)  ) as cids,  " +
            " @l \\:= @l+1 as lvl " +
            " FROM department, " +
            "(SELECT @ids \\:= ?1 , @l \\:= 0 ) b " +
            "WHERE @ids IS NOT NULL) id," +
            " department DATA WHERE FIND_IN_SET(DATA.id, ID._ids) ",nativeQuery=true)
    List<Long> allChild(Long department_id);

    @Query("select dep from Department dep order by dep.level,dep.orderNo")
    List<Department> listDepartments();
    @Query("select dep from Department dep where dep.id not in (select wg.departmentId from WorkGuide wg where wg.type in (1,100))")
    List<Department> missingWorkGuide();
}
