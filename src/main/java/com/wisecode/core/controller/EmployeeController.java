package com.wisecode.core.controller;

import com.wisecode.core.RoleName;
import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.conf.secuirty.exception.AppException;
import com.wisecode.core.entities.Employee;
import com.wisecode.core.entities.Role;
import com.wisecode.core.entities.User;
import com.wisecode.core.payload.*;
import com.wisecode.core.repositories.DepartmentRepository;
import com.wisecode.core.repositories.EmployeeRepository;
import com.wisecode.core.repositories.RoleRepository;
import com.wisecode.core.repositories.UserRepository;
import com.wisecode.core.util.SystemUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/employee")
@Log4j2
public class EmployeeController extends GenericController<Employee>{
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    EmployeeRepository repository;
    @Autowired
    public EmployeeController(EmployeeRepository repo) {
        super(repo);
        this.repository = repo;
    }

    @GetMapping("/listAll")
    public List<Employee> retrieveAllStudents() {
        return repository.findAll();
    }

    @PostMapping("/list")
    public ResponseEntity<DataTableResponse> getList(@Valid @RequestBody DataTableRequest<CustomData> request,
                                     @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
        Long empNo = null;
        try{
            empNo = Long.parseLong(request.getData().getTextSearch());
        }catch(Exception ex){}
        String search_txt = request.getData().getTextSearch()== null || empNo!=null?null :"%"+request.getData().getTextSearch()+"%" ;
        DataTableResponse response = new DataTableResponse();
        Page<Employee> page = repository.search(search_txt,empNo,pageRequest);
        List<Employee> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        response.setData(list);
        return ResponseEntity.ok().body(response);
    }
@PostMapping("/listByDepartments")
    public ResponseEntity<DataTableResponse> listEmployeeByDepartments( @RequestBody DataTableRequest<GenericData> request,
                                     @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
    String depEnc = request.getData().getData().get("encId").toString();
    Long dep_id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(depEnc)));
    DataTableResponse response = new DataTableResponse();
    List<Long> departmentList = departmentRepository.allChild(dep_id);
    Page<Employee> page = repository.listEmployeeByDepartments(departmentList,pageRequest);
    List<Employee> list =page.getContent();
    response.setCurrentPage(request.getCurrentPage());
    response.setPageSize(request.getPageSize());
    response.setTotal(page.getTotalElements());
    response.setData(list);
    return ResponseEntity.ok().body(response);
}
    @PostMapping("/listByDepartment/{encId}")
    public ResponseEntity<List<Employee>> listEmployeeByDepartment( @PathVariable String encId,
                                                                        @CurrentUser User currentUser){
        Long dep_id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
        List<Long> departmentList = departmentRepository.allChild(dep_id);
        Pageable request = PageRequest.of(0,Integer.MAX_VALUE);
        Page<Employee> page = repository.listEmployeeByDepartments(departmentList,request);
        List<Employee> list =page.getContent();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/search")
    public ResponseEntity<DataTableResponse> search(@Valid @RequestBody DataTableRequest<EmployeeSearchData> request,
                                                     @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
        String employeeName = request.getData().getEmployeeName()== null?"%%" :"%"+request.getData().getEmployeeName()+"%" ;
        Long employeeNumber = request.getData().getEmployeeNumber() ;
        DataTableResponse response = new DataTableResponse();
        Page<Employee> page = repository.search(employeeName,employeeNumber,pageRequest);
        List<Employee> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        response.setData(list);
        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/searchByDepartment")
    public ResponseEntity<DataTableResponse> searchByDepartment(@Valid @RequestBody DataTableRequest<EmployeeSearchData2> request,
                                                    @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
        String employeeName = request.getData().getEmployeeName()== null?"%%" :"%"+request.getData().getEmployeeName()+"%" ;
        Long employeeNumber = request.getData().getEmployeeNumber() ;
        Long departmentId = request.getData().getDepartmentId();
        DataTableResponse response = new DataTableResponse();
        Page<Employee> page = repository.searchByDepartment(employeeName,employeeNumber,departmentId,pageRequest);
        List<Employee> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        response.setData(list);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/create")
    ResponseEntity<Employee> createEmployee(@Validated @RequestBody Employee json) {
        User user = json.getUser();
        user.setEmployee(json);
        json.setDeleted(false);
        json.setUser(user);
        user.setActive(true);
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));
        user.setRoles(Collections.singleton(userRole));
        user.setPassword(passwordEncoder.encode("12345678"));
        repository.save(json);
        return ResponseEntity.ok(json);
    }
    @PostMapping(value = "/save/{id}")
    ResponseEntity<Employee> save(@Validated @RequestBody Employee json, @PathVariable String id) {
        long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
        Employee entity = repository.findById(_id).orElse(null);
        if (entity != null) {
            User user = entity.getUser();
            user.setUsername(json.getUser().getUsername());
            user.setActive(json.getUser().getActive());
            json.setUser(user);
            BeanUtils.copyProperties(json, entity);
            repository.save(entity);
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.badRequest().build();
    }

    @Transactional
    @PostMapping(value = "/resetPassword")
    public Integer resetPassword(@Validated @RequestBody String data, @CurrentUser User user){
        if(data != null){
            Long id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(data)));
            String pass = passwordEncoder.encode("12345678");
            return userRepository.updatePassword(pass,id);
        }
        return 0;
    }

    @Transactional
    @PostMapping(value = "/changePassword")
    public Integer changePassword(@Validated @RequestBody List<PairData<String,String>> data,
                                  @CurrentUser User cuser){
        String oldPassword ="";
        String newPassword = "";
        String encId = "";
        for (PairData<String , String> d : data){
            if(!d.getKey().isEmpty() && d.getKey().equals("oldPassword")){
                oldPassword = d.getValue();
            }
            if(!d.getKey().isEmpty() && d.getKey().equals("newPassword")){
                newPassword = d.getValue();
            }
            if(!d.getKey().isEmpty() && d.getKey().equals("encId")){
                encId = d.getValue();
            }
        }
        Long id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
        if(id.equals(cuser.getId())){
            User user = userRepository.findById(id).orElse(null);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), oldPassword, user.getAuthorities());
            if(authentication.isAuthenticated()){
               int rows =  userRepository.updatePassword(passwordEncoder.encode(newPassword),id);
            }
        }

        return 0;
    }

    @PostMapping(value = "/findEmployee")
    ResponseEntity<List<Employee>> findEmployeeByNameOrNo(@Validated @RequestBody List<PairData<String,String>> data){
        String full_name ="";
        String emp_no = "";
        for (PairData<String , String> d : data){
            if(!d.getKey().isEmpty() && d.getKey().equals("full_name")){
                full_name = d.getValue();
                full_name = full_name == null?"%%":"%"+full_name+"%";
            }
        }

        List<Employee> employees = repository.findEmployeeByName(full_name);
        if(employees!=null){
            return ResponseEntity.ok(employees);
        }else{
            return ResponseEntity.badRequest().build();
        }

    }
}
