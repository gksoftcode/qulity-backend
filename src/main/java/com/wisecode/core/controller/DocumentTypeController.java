package com.wisecode.core.controller;

import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.entities.DocumentType;
import com.wisecode.core.entities.Job;
import com.wisecode.core.entities.User;
import com.wisecode.core.payload.CustomData;
import com.wisecode.core.payload.DataTableRequest;
import com.wisecode.core.payload.DataTableResponse;
import com.wisecode.core.repositories.DocumentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

public class DocumentTypeController extends GenericController<DocumentType>{

    DocumentTypeRepository repository;

    @Autowired
    public DocumentTypeController(DocumentTypeRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Secured({"ROLE_HR","ROLE_ADMIN"})
    @PostMapping("/list")
    public ResponseEntity<?> getList(@Valid @RequestBody DataTableRequest<CustomData> request,
                                     @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
        String search_txt = request.getData().getTextSearch()== null?"%%" :"%"+request.getData().getTextSearch()+"%" ;
        DataTableResponse response = new DataTableResponse();
        Page<DocumentType> page = repository.findAllByNameIsLike(search_txt,pageRequest);
        List<DocumentType> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        response.setData(list);
        return ResponseEntity.ok().body(response);
    }
}
