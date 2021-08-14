package com.wisecode.core.controller;

import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.entities.Department;
import com.wisecode.core.entities.User;
import com.wisecode.core.payload.PairData;
import com.wisecode.core.repositories.DepartmentRepository;
import com.wisecode.core.util.SystemUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/department")
@Log4j2
public class DepartmentController extends GenericController<Department> {
    DepartmentRepository repository;
    
    @Autowired
    public DepartmentController(DepartmentRepository repo) {
        super(repo);
        this.repository = repo;
    }

    @GetMapping("/root")
    public ResponseEntity<List<Department>> getRoot(){
        Department root = repository.findDepartmentByParentDepartmentIsNull();
        List<Department> lst = new ArrayList<>(1) ;
        lst.add(root);
        return ResponseEntity.ok().body(lst);
    }
    @GetMapping("/listDepartments")
    public ResponseEntity<List<Department>> getListDepartments(){
        List<Department> lst = repository.listDepartments();
        return ResponseEntity.ok().body(lst);
    }

    @Transactional
    @PostMapping("/setManager/{depId}/{managerId}")
    public ResponseEntity<Boolean> setManager(@PathVariable String depId, @PathVariable String managerId, @CurrentUser User user){
           Long manager_id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(managerId))) ;
           Long dep_id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(depId))) ;
           Integer rows = repository.setManager(manager_id,dep_id);
           if(rows >0 ){
               return ResponseEntity.ok(true);
           }else{
               return ResponseEntity.ok(false);
           }
    }

    @Transactional
    @PostMapping("/reOrder")
    public void reOrder(@RequestBody List<PairData<Long, Long>> data, @CurrentUser User user){
       for(PairData<Long, Long> elem:data){
           repository.setOrderNo(elem.getKey(),elem.getValue().intValue());
       }
    }

    @Override
    protected boolean canDelete(Long id) {
     long total =  repository.countAttachmentByDepartmentId(id)+ repository.countEmployeeByDepartmentId(id)+repository.countWorkGuideByDepartmentId(id)+repository.countAuditPlanByDepartmentId(id);
     return total == 0;
    }
}
