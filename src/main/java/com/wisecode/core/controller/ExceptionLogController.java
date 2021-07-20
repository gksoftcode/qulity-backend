package com.wisecode.core.controller;

import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.dto.AttachmentDto;
import com.wisecode.core.dto.ExceptionLogDTO;
import com.wisecode.core.entities.Attachment;
import com.wisecode.core.entities.AuditPlan;
import com.wisecode.core.entities.ExceptionLog;
import com.wisecode.core.entities.User;
import com.wisecode.core.payload.DataTableRequest;
import com.wisecode.core.payload.DataTableResponse;
import com.wisecode.core.payload.GenericData;
import com.wisecode.core.repositories.ExceptionLogRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/exception_log")
@Log4j2
public class ExceptionLogController extends GenericController<ExceptionLog>{
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    ExceptionLogRepository  repository;

    public ExceptionLogController(ExceptionLogRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @PostMapping("/search")
    public ResponseEntity<DataTableResponse> search(@Valid @RequestBody DataTableRequest<GenericData> request,
                                                    @CurrentUser User currentUser) throws ParseException {
        PageRequest pageRequest = preparePageRequest(request);
        String sortBy = request.getSortBy();
        sortBy = sortBy.replaceAll("([^_A-Z])([A-Z])", "$1_$2");
        sortBy =  sortBy.length()==0?"":" order by " + sortBy+ (request.getSortDesc()?" desc ":"");
        String fromDate = request.getData().getData().get("fromDate").toString();
        Date dateFrom=new SimpleDateFormat("yyyy-MM-dd").parse(fromDate);
        String toDate = request.getData().getData().get("toDate").toString();
        Date dateTo=new SimpleDateFormat("yyyy-MM-dd").parse(toDate);
        String empId = request.getData().getData().get("empId").toString();
        String id = request.getData().getData().get("id").toString();
        SqlParameterSource parameter =  new MapSqlParameterSource()
                .addValue("fromDate",dateFrom)
                .addValue("toDate",dateTo)
                .addValue("empId",empId==null?-1:Long.parseLong(empId))
                .addValue("id",id==null?-1:Long.parseLong(id))
                .addValue("currentPage",pageRequest.getPageNumber()*pageRequest.getPageSize())
                .addValue("pageSize",pageRequest.getPageSize());
        List<ExceptionLogDTO> lst = namedParameterJdbcTemplate.query("select ex_log.id,ex_log.message,ex_log.service_url  serviceUrl," +
                "ex_log.create_date  createdDate, ex_log.exception_name  exceptionName ,emp.full_name  employeeName from exception_log ex_log " +
                "left join employee emp on emp.id = ex_log.user_number " +
                "where (ex_log.create_date between :fromDate and :toDate) and " +
                "(:empId = -1 or ex_log.user_number = :empId) and (:id = -1 or ex_log.id = :id)" +
                sortBy+
                " limit :pageSize offset :currentPage " ,parameter,  BeanPropertyRowMapper.newInstance(ExceptionLogDTO.class));
        Long cnt = namedParameterJdbcTemplate.queryForObject("select count(*)  from exception_log ex_log " +
                " left join employee emp on emp.id = ex_log.user_number " +
                " where (ex_log.create_date between :fromDate and :toDate) and  " +
                "  (:empId = -1 or ex_log.user_number = :empId) and (:id = -1 or ex_log.id = :id)",parameter,Long.class);
        DataTableResponse response = new DataTableResponse();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(cnt);
        response.setData(lst);
        return ResponseEntity.ok().body(response);
    }
}
