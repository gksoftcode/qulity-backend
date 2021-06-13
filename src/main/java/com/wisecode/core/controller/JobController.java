package com.wisecode.core.controller;

import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.entities.Job;
import com.wisecode.core.entities.User;
import com.wisecode.core.payload.CustomData;
import com.wisecode.core.payload.DataTableRequest;
import com.wisecode.core.payload.DataTableResponse;
import com.wisecode.core.repositories.JobRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/job")
@Log4j2

public class JobController extends GenericController<Job>{

    JobRepository repository;

    @Autowired
    public JobController(JobRepository repo) {
        super(repo);
        this.repository = repo;
    }

    @Secured({"ROLE_HR","ROLE_ADMIN"})
    @PostMapping("/list")
    public ResponseEntity<?> getList(@Valid @RequestBody DataTableRequest<CustomData> request,
    @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
        String search_txt = request.getData().getTextSearch()== null?"%%" :"%"+request.getData().getTextSearch()+"%" ;
        DataTableResponse response = new DataTableResponse();
        Page<Job> page = repository.findAllByNameIsLike(search_txt,pageRequest);
       List<Job> list =page.getContent();
       response.setCurrentPage(request.getCurrentPage());
       response.setPageSize(request.getPageSize());
       response.setTotal(page.getTotalElements());
       response.setData(list);
       return ResponseEntity.ok().body(response);
    }
}
