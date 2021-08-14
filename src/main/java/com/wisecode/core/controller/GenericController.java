package com.wisecode.core.controller;


import com.wisecode.core.entities.BaseEntity;
import com.wisecode.core.exceptions.EmployeeNotFoundException;
import com.wisecode.core.payload.CustomData;
import com.wisecode.core.payload.DataTableRequest;
import com.wisecode.core.payload.DataTableResponse;
import com.wisecode.core.util.SystemUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Log4j2
public abstract class GenericController<T extends BaseEntity> {

    private JpaRepository<T, Long> repository;


    public GenericController(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    @PostMapping(value = "/save/{id}")
    ResponseEntity<T> save(@Validated @RequestBody T json, @PathVariable String id) {
        if (id.equals("-1")) {
            json = repository.save(json);
            return ResponseEntity.ok(json);
        } else {
            long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
            T entity = repository.findById(_id).orElse(null);
            if (entity != null) {
                BeanUtils.copyProperties(json, entity);
                entity = repository.save(entity);
                return ResponseEntity.ok(entity);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/listAll",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<T> listAll() {
        return this.repository.findAll();
    }

    @PostMapping(consumes={MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody Map<String, Object> create(@RequestBody T json) {
        log.debug("create() with body {} of type {}", json, json.getClass());

        T created = this.repository.save(json);

        Map<String, Object> m = new HashMap<>();
        m.put("success", true);
        m.put("created", created);
        return m;
    }

    @GetMapping(value="/get/{id}")
    public @ResponseBody T get(@PathVariable Long id) {
        Optional<T> object = repository.findById(id);

        if (!object.isPresent())
            throw new NullPointerException();

        return object.get();
    }
    @GetMapping(value="/{id}")
    public @ResponseBody T get(@PathVariable String id) {
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
        Optional<T> object = repository.findById(_id);
        if (!object.isPresent())
            throw new NullPointerException();

        return object.get();
    }

    @PostMapping(value="/update/{id}", consumes={MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody Map<String, Object> update(@PathVariable Long id, @RequestBody T json) {
        log.debug("update() of id#{} with body {}", id, json);
        log.debug("T json is of type {}", json.getClass());

        T entity = this.repository.findById(id).get();
        try {
            BeanUtils.copyProperties(entity, json);
        }
        catch (Exception e) {
            log.warn("while copying properties", e);
        }

        log.debug("merged entity: {}", entity);

        T updated = this.repository.save(entity);
        log.debug("updated enitity: {}", updated);

        Map<String, Object> m = new HashMap<>();
        m.put("success", true);
        m.put("id", id);
        m.put("updated", updated);
        return m;
    }

    protected PageRequest preparePageRequest(DataTableRequest<?> request){
        PageRequest pageRequest = null;
        if(request.getSortBy() != null && request.getSortBy().trim().length()> 0){
            Sort sort = Sort.by(request.getSortBy());
            if(request.getSortDesc()!=null){
                if(request.getSortDesc()){
                    sort = sort.descending();
                }else{
                    sort = sort.ascending();
                }
            }
            pageRequest = PageRequest.of(request.getCurrentPage(),request.getPageSize(),sort);
        }else {
            pageRequest=PageRequest.of(request.getCurrentPage(), request.getPageSize());
        }
        return pageRequest;
    }

    @DeleteMapping(value="/{id}")
    public @ResponseBody Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> m = new HashMap<>();
        if(canDelete(id)) {
            this.repository.deleteById(id);
            m.put("success", true);
        }else{
            m.put("success", false);
        }
        return m;
    }

    protected boolean canDelete(Long id){
        return true;
    }
}