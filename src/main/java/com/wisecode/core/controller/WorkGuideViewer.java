package com.wisecode.core.controller;


import com.wisecode.core.entities.WorkGuide;
import com.wisecode.core.repositories.WorkGuideRepository;
import com.wisecode.core.util.SystemUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@CrossOrigin(origins = "*", allowedHeaders = "*",methods = {RequestMethod.GET,
        RequestMethod.HEAD,
        RequestMethod.OPTIONS,
        RequestMethod.DELETE,
        RequestMethod.POST,
        RequestMethod.PUT})
@RestController
@RequestMapping("/view/workGuide")
@Log4j2
public class WorkGuideViewer {
    WorkGuideRepository repository;

    public WorkGuideViewer(WorkGuideRepository repository) {
        this.repository = repository;
    }
    @CrossOrigin
    @PostMapping(value = "/getByDepartment/{departmentId}/{type}")
    ResponseEntity<WorkGuide> getByDepartment(@PathVariable String departmentId, @PathVariable Integer type){
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(departmentId)));
        WorkGuide object = repository.findByDepartmentIdAndType(_id,type);
        if(object == null){
            return ResponseEntity.badRequest().build();
        }else{
            return ResponseEntity.ok(object);
        }

    }
}
