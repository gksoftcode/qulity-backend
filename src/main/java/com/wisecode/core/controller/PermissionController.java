package com.wisecode.core.controller;

import com.wisecode.core.entities.*;
import com.wisecode.core.payload.DataTableRequest;
import com.wisecode.core.payload.DataTableResponse;
import com.wisecode.core.payload.PairData;
import com.wisecode.core.repositories.EmployeeRepository;
import com.wisecode.core.repositories.PermissionLogRepository;
import com.wisecode.core.repositories.RoleRepository;
import com.wisecode.core.repositories.UserRepository;
import com.wisecode.core.util.SystemUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CrossOrigin(origins = "*", allowedHeaders = "*",methods = {RequestMethod.GET,
        RequestMethod.HEAD,
        RequestMethod.OPTIONS,
        RequestMethod.DELETE,
        RequestMethod.POST,
        RequestMethod.PUT})
@RestController
@RequestMapping("/api/permission")
@Log4j2
public class PermissionController extends GenericController<Role>{

    public static final int LOG_INSERT = 1;
    public static final int LOG_DELETE = 2;

    RoleRepository repository;

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PermissionLogRepository logRepository;
    @Autowired
    public PermissionController(RoleRepository repo) {
        super(repo);
        this.repository = repo;
    }
    @CrossOrigin
    @PostMapping(value = "/listAll")
    public ResponseEntity<List<Role>> listAllPermission(){
        List<Role> lst = repository.findAll();
        return ResponseEntity.ok(lst);
    }

    @CrossOrigin
    @PostMapping(value = "/listEmployeeByPermission/{permID}")
    public ResponseEntity<DataTableResponse> listEmployeeByPermission(@Valid @RequestBody DataTableRequest<List<PairData<String,String>>> request, @PathVariable String permID){
        Long _id = null;
        Long emp_id = null;
        if(permID != null && !permID.equals("-1")){
            _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(permID)));
        }
        if(request.getData()!= null){
            for (PairData<String,String> pd : request.getData()){
                if(pd.getKey().equals("employeeId")){
                    try {
                        emp_id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(pd.getValue().toString())));
                    }catch(Exception ignored){

                    }
                }
            }
        }
        PageRequest pageRequest = preparePageRequest(request);
        Page<EmployeeRole> page = employeeRepository.listEmployeeByPermission(emp_id,_id,pageRequest);
        DataTableResponse response = new DataTableResponse();
        List<EmployeeRole> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        response.setData(list);
        return ResponseEntity.ok(response);
    }
    @CrossOrigin
    @PostMapping(value = "/addPermissionToEmployee")
    public ResponseEntity<Map<String, Object>> addPermissionToEmployee(@Valid @RequestBody List<PairData<String,String>> request){
        Long _id = null;
        Long emp_id = null;
            for (PairData<String,String> pd : request){
                if(pd.getKey().equals("employeeId")){
                    try {
                        emp_id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(pd.getValue().toString())));
                    }catch(Exception ignored){

                    }
                }else if(pd.getKey().equals("permissionId")){
                    try {
                        _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(pd.getValue().toString())));
                    }catch(Exception ignored){

                    }
                }
            }
        if(_id == null || emp_id == null){
            Map<String, Object> m = new HashMap<>();
            m.put("success", false);
            m.put("id", _id);
            m.put("messageId",2);
            return ResponseEntity.ok(m);
        }
        Employee emp = employeeRepository.findById(emp_id).orElse(null);
        if(emp!= null){
            Role role = repository.getOne(_id);
            User user = userRepository.findById(emp.getUser().getId()).orElse(null);
            if(user.getRoles().contains(role)){
                Map<String, Object> m = new HashMap<>();
                m.put("success", false);
                m.put("id", _id);
                m.put("messageId",1);
                return ResponseEntity.ok(m);
            }
            user.getRoles().add(role);
            userRepository.save(user);
            PermissionLog log = new PermissionLog();
            log.setEmployee(emp);
            log.setRole(role);
            log.setActionType(LOG_INSERT);
            logRepository.save(log);
            Map<String, Object> m = new HashMap<>();
            m.put("success", true);
            m.put("id", _id);
            m.put("messageId",0);
            return ResponseEntity.ok(m);
        }
        return ResponseEntity.badRequest().build();
    }

    @CrossOrigin
    @PostMapping(value = "/deletePermissionFromEmployee")
    public ResponseEntity<Map<String, Object>> deletePermissionFromEmployee(@Valid @RequestBody List<PairData<String,String>> request){
        Long _id = null;
        Long emp_id = null;
        for (PairData<String,String> pd : request){
            if(pd.getKey().equals("employeeId")){
                try {
                    emp_id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(pd.getValue().toString())));
                }catch(Exception ignored){

                }
            }else if(pd.getKey().equals("permissionId")){
                try {
                    _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(pd.getValue().toString())));
                }catch(Exception ignored){

                }
            }
        }
        if(_id == null || emp_id == null){
            Map<String, Object> m = new HashMap<>();
            m.put("success", false);
            m.put("id", _id);
            m.put("messageId",4);
            return ResponseEntity.ok(m);
        }
        Employee emp = employeeRepository.findById(emp_id).orElse(null);
        if(emp!= null){
            Role role = repository.getOne(_id);
            User user = userRepository.findById(emp.getUser().getId()).orElse(null);
            user.getRoles().remove(role);
            userRepository.save(user);
            PermissionLog log = new PermissionLog();
            log.setEmployee(emp);
            log.setRole(role);
            log.setActionType(LOG_DELETE);
            logRepository.save(log);
            Map<String, Object> m = new HashMap<>();
            m.put("success", true);
            m.put("id", _id);
            m.put("messageId",3);
            return ResponseEntity.ok(m);
        }
        return ResponseEntity.badRequest().build();
    }

}
