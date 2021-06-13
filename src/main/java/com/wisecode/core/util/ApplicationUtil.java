package com.wisecode.core.util;

import com.wisecode.core.RoleName;
import com.wisecode.core.entities.Department;
import com.wisecode.core.entities.Employee;
import com.wisecode.core.entities.Role;
import com.wisecode.core.entities.User;
import com.wisecode.core.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ApplicationUtil {
    @Autowired
    RoleRepository roleRepository;

    List<Role> roles;

    @PostConstruct
    private void init(){
        roles = roleRepository.findAll();
    }

    public boolean hasPermission(User user, RoleName roleName){
        Role r = new Role();
        r.setName(roleName);
       // Role role = roles.stream().filter(rl -> rl.equals(r)).findFirst().orElse(null);
        if(user.getRoles() != null){
           Role role= user.getRoles().stream().filter(rl -> rl.equals(r)).findFirst().orElse(null);
           return role != null;
        }
        return false;
    }

    public boolean isManager( Employee employee){
        if(employee != null && employee.getDepartment()!=null&& employee.getDepartment().getManager()!=null){
            return employee.getDepartment().getManager().equals(employee);
        }
        return false;
    }
    public boolean isSuperManager( Employee employee, Department department){
        return isManager(employee) &&
                employee.getDepartment().getDepartmentList()!=null &&
                employee.getDepartment().getDepartmentList().contains(department);
    }
}
