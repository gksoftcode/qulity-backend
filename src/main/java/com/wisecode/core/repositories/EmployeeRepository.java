package com.wisecode.core.repositories;

import com.wisecode.core.entities.Employee;
import com.wisecode.core.entities.EmployeeRole;
import com.wisecode.core.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    boolean existsByEmpNumberAndIdNot(Long empNumber,Long id);
    boolean existsByEmpNumber(Long empNumber);

    Employee findByUser(User user);
    Page<Employee> findAllByFullNameIsLikeAndDeletedIsFalse(String name, Pageable pageable);
    @Query("select emp from Employee emp where (:emp_name is null or emp.fullName like :emp_name) and (:emp_no is null or emp.empNumber = :emp_no)")
    Page<Employee> search(@Param("emp_name") String name,@Param("emp_no")Long emp_no, Pageable pageable);

    @Query("select emp from Employee emp where (:emp_name is null or emp.fullName like :emp_name)" +
            " and (:emp_no is null or emp.empNumber = :emp_no)" +
            " and emp.departmentId = :dep_id")
    Page<Employee> searchByDepartment(@Param("emp_name") String name,@Param("emp_no")Long emp_no,
                                      @Param("dep_id")Long dep_id, Pageable pageable);

    @Query("select emp from Employee emp where emp.department.id in (:departments)")
    Page<Employee> listEmployeeByDepartments(@Param("departments") List<Long> departments, Pageable pageable);

    @Query("select emp from Employee emp where  emp.fullName like :employeeName")
    List<Employee> findEmployeeByName(@Param("employeeName") String employeeName);

    @Query("select new com.wisecode.core.entities.EmployeeRole(emp,rl)  from Employee emp " +
            "inner join emp.user usr " +
            "inner join usr.roles rl " +
            "where (:employeeId is null or emp.id = :employeeId) and (:roleId is null or rl.id = :roleId)")
    Page<EmployeeRole> listEmployeeByPermission(@Param("employeeId") Long employeeId,@Param("roleId") Long roleId, Pageable pageable);

    @Query("select emp from Employee emp where emp.user.id = :userId")
    Employee findByUserId(Long userId);
}
