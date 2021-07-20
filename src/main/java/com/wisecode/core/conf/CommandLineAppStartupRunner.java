package com.wisecode.core.conf;

import com.wisecode.core.RoleName;
import com.wisecode.core.conf.secuirty.exception.AppException;
import com.wisecode.core.entities.*;
import com.wisecode.core.repositories.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Log4j2
public class CommandLineAppStartupRunner implements CommandLineRunner{

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        // TODO Auto-generated method stub
        List<Role> roles = roleRepository.findAll();
        if(roles.size() == 0){
            log.info("first application start ....");
            Role role = new Role();
            role.setName(RoleName.ROLE_ADMIN);
            roleRepository.save(role);
            Role role2 = new Role();
            role2.setName(RoleName.ROLE_USER);
            roleRepository.save(role2);
        }
        User users = userRepository.findByUsername("admin").orElse(null);
        if(users == null){
            User admin = new User();
            admin.setUsername("admin");
            admin.setActive(true);
            admin.setPassword(passwordEncoder.encode("00@dmiN00"));
            Role userRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new AppException("User Role not set."));
            admin.setRoles(Collections.singleton(userRole));
            Employee adminEmp = new Employee();
            adminEmp.setDeleted(false);
            adminEmp.setFullName("Admin");
            adminEmp.setShortName("admin");
            adminEmp.setEmpNumber(0L);
            adminEmp.setMobileNumber("");
            adminEmp.setGender(true);
            adminEmp.setEmail("");
            adminEmp.setUser(admin);
            admin.setEmployee(adminEmp);
            employeeRepository.save(adminEmp);
        }
        if(departmentRepository.count() == 0){
            Department department = new Department();
            department.setDeleted(false);
            department.setLevel(0);
            department.setParentId(null);
            department.setName("هيكل الإدارة");
            departmentRepository.save(department);
        }
    }

}