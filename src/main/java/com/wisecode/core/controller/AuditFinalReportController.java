package com.wisecode.core.controller;

import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.entities.AuditFinalReport;
import com.wisecode.core.entities.AuditPlan;
import com.wisecode.core.entities.Employee;
import com.wisecode.core.entities.User;
import com.wisecode.core.payload.DataTableRequest;
import com.wisecode.core.payload.DataTableResponse;
import com.wisecode.core.payload.GenericData;
import com.wisecode.core.repositories.AuditFinalReportRepository;
import com.wisecode.core.util.SystemUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/auditReport")
@Log4j2
public class AuditFinalReportController extends GenericController<AuditFinalReport> {

    public static final int POINT_TYPE_STRENGTH= 1;
    public static final int POINT_TYPE_WEAKNESS = 2;

    AuditFinalReportRepository repository;

    @Autowired
    public AuditFinalReportController(AuditFinalReportRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @PostMapping("/listEmployees/{encId}")
    public ResponseEntity<List<Employee>> listEmployees(@PathVariable String encId){
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
        return ResponseEntity.ok(repository.employeesByReportId(_id));
    }

    @PostMapping("/search")
    public ResponseEntity<DataTableResponse> search(@Valid @RequestBody DataTableRequest<GenericData> request,
                                                    @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
        String year_id = request.getData().getData().get("yearId")==null?null:request.getData().getData().get("yearId").toString();
        String status = request.getData().getData().get("status")==null?null:request.getData().getData().get("status").toString();
        String title = request.getData().getData().get("title")!=null?request.getData().getData().get("title").toString():"";
        DataTableResponse response = new DataTableResponse();
        Page<AuditFinalReport> page = repository.search(title,year_id == null?null :Integer.parseInt(year_id),
                status == null?null :Integer.parseInt(status),pageRequest);
        List<AuditFinalReport> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        response.setData(list);
        return ResponseEntity.ok().body(response);
    }
}
