package com.wisecode.core.controller;

import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.dto.AttachmentDto;
import com.wisecode.core.dto.AttachmentTransactionDto;
import com.wisecode.core.entities.*;
import com.wisecode.core.mapper.AttachmentTransactionMapper;
import com.wisecode.core.payload.DataTableRequest;
import com.wisecode.core.payload.DataTableResponse;
import com.wisecode.core.payload.GenericData;
import com.wisecode.core.repositories.*;
import com.wisecode.core.util.SystemUtil;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/attachment")
@Log4j2
public class AttachmentController  extends GenericController<Attachment>{
    public static final int STATUS_NEW = 0;
    public static final int STATUS_EDITED = 5;
    public static final int STATUS_REJECTED = 6;
    public static final int STATUS_SUBMITTED = 10;
    public static final int STATUS_APPROVED = 20;
    public static final int STATUS_OLD_VERSION = 30;
    public static final int STATUS_CANCELED = 50;

    @Autowired
    AttachmentRepository repository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    AttachmentTransactionRepository attachmentTransactionRepository;
    @Autowired
    ProcedureStepRepository procedureStepRepository;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public AttachmentController(JpaRepository<Attachment, Long> repository) {
        super(repository);
    }

    @CrossOrigin
    @PostMapping(value = "/save")
    ResponseEntity<Attachment> save(@RequestParam("fileData") MultipartFile fileData,
                                    @RequestParam("name") String name,
                                    @RequestParam("version") Integer version,
                                    @RequestParam("orderNo") Integer orderNo,
                                    @RequestParam("issueDate") String issueDate,
                                    @RequestParam("departmentId") Long departmentId, @CurrentUser User user) throws IOException, ParseException {
            Attachment attachment = new Attachment();
            attachment.setFileData(fileData.getBytes());
            attachment.setName(name);
            attachment.setVersion(version);
            attachment.setOrderNo(orderNo);
            attachment.setFileName(fileData.getOriginalFilename());
            attachment.setMimeType(fileData.getContentType());
            Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(issueDate);
            attachment.setIssueDate(date1);
            attachment.setUuid(UUID.randomUUID().toString());
            attachment.setDepartmentId(departmentId);
            attachment = repository.save(attachment);
            addTransaction(attachment);
            return ResponseEntity.ok(attachment);

    }

    @CrossOrigin
    @PostMapping(value = "/save2")
    ResponseEntity<Attachment> save2(@RequestParam("fileData") MultipartFile fileData,
                                    @RequestParam("name") String name,
                                    @RequestParam("version") Integer version,
                                    @RequestParam("orderNo") Integer orderNo,
                                    @RequestParam("issueDate") String issueDate,
                                    @RequestParam("departmentId") String departmentId, @CurrentUser User user) throws IOException, ParseException {
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(departmentId)));
        Attachment attachment = new Attachment();
        attachment.setFileData(fileData.getBytes());
        attachment.setName(name);
        attachment.setVersion(version);
        attachment.setOrderNo(orderNo);
        attachment.setFileName(fileData.getOriginalFilename());
        attachment.setMimeType(fileData.getContentType());
        Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(issueDate);
        attachment.setIssueDate(date1);
        attachment.setStatus(STATUS_NEW);
        attachment.setUuid(UUID.randomUUID().toString());
        attachment.setDepartmentId(_id);
        attachment = repository.save(attachment);
        addTransaction(attachment);
        return ResponseEntity.ok(attachment);
    }

    @CrossOrigin
    @PostMapping(value = "/edit/{encId}")
    ResponseEntity<AttachmentDto> edit(@PathVariable String encId,
                                    @RequestParam(value = "tempFileData",required = false) MultipartFile tempFileData,
                                     @RequestParam(value = "pdfFileData",required = false) MultipartFile pdfFileData,
                                     @RequestParam(value = "name",required = false) String name,
                                     @RequestParam(value = "version",required = false) Integer version,
                                     @RequestParam(value = "orderNo",required = false) Integer orderNo,
                                     @RequestParam(value = "issueDate",required = false) String issueDate,
                                    @RequestParam(value = "updateState",required = false,defaultValue = "0") Integer updateState,
                                    @CurrentUser User user) throws IOException, ParseException {
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
        Attachment attachment = repository.findById(_id).orElse(null);
        if(attachment != null){
            if(tempFileData != null) {
                attachment.setTempFileData(tempFileData.getBytes());
                attachment.setFileTempName(tempFileData.getOriginalFilename());
                attachment.setMimeTempType(tempFileData.getContentType());
            }
            if(pdfFileData!=null) {
                attachment.setPdfFileData(pdfFileData.getBytes());
                attachment.setFilePdfName(pdfFileData.getOriginalFilename());
                attachment.setMimePdfType(pdfFileData.getContentType());
            }
            attachment.setName(name==null? attachment.getName() : name);
            attachment.setVersion(version==null?attachment.getVersion():version);
            attachment.setOrderNo(orderNo==null?attachment.getOrderNo():orderNo);
            if(issueDate != null) {
                Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(issueDate);
                attachment.setIssueDate(date1);
            }
            attachment.setStatus(updateState==1?STATUS_SUBMITTED: STATUS_EDITED);
            attachment = repository.save(attachment);
            addTransaction(attachment);
            return ResponseEntity.ok(attachmentToDto(attachment));
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @CrossOrigin
    @PostMapping(value = "/update/{id}")
    ResponseEntity<Attachment> Update(@Validated @RequestBody Attachment json, @PathVariable String id, @CurrentUser User user) throws Exception {
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
        Attachment entity = repository.findById(_id).orElse(null);
        if (entity != null) {
            entity.setName(json.getName());
            entity.setVersion(json.getVersion());
            entity.setOrderNo(json.getOrderNo());
            entity.setIssueDate(json.getIssueDate());
            repository.save(entity);
            addTransaction(entity);
            return ResponseEntity.ok(entity);
        }else{
            throw new Exception("Attachment not found");
        }

    }
    @CrossOrigin
    @PostMapping(value = "/delete/{id}")
    ResponseEntity<Map<String, Object>> Update(@PathVariable String id, @CurrentUser User user) {
            Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
            Attachment at = new Attachment();
            at.setId(_id);
            long cnt = repository.countForDelete(at);
            if(cnt == 0){
              repository.delete(at);
              Map<String, Object> m = new HashMap<>();
                m.put("success", true);
                m.put("id", _id);
                return ResponseEntity.ok(m);
            }else{
                Map<String, Object> m = new HashMap<>();
                m.put("success", false);
                m.put("id", _id);
                m.put("count", cnt);
                return ResponseEntity.ok(m);
            }
    }
    @PostMapping(value = "/list/{departmentId}")
    ResponseEntity<List<Attachment>> getListByDepartment(@PathVariable String departmentId){
        Long _id =Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(departmentId)));
        List<Attachment> list = repository.findByDepartmentIdOrderByOrderNo(_id);
        return ResponseEntity.ok(list);
    }
    @PostMapping(value = "/search")
    ResponseEntity<?> search(@Valid @RequestBody DataTableRequest<GenericData> request,
                                            @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
        String str_id = request.getData().getData().get("departmentId").toString();
        List<Long> departmentList = null;
        if(!str_id.equals("-1")){
            try {
                Long dep_id = Long.parseLong(Objects.requireNonNull(
                        SystemUtil.decrypt(request.getData().getData().get("departmentId").toString())));
                departmentList = departmentRepository.allChild(dep_id);
            }catch (Exception ex){
                return ResponseEntity.badRequest().build();
            }
        }
        Integer status_id = Integer.parseInt(request.getData().getData().get("statusId").toString());
        DataTableResponse response = new DataTableResponse();
        Page<Attachment> page = repository.search(departmentList,status_id,departmentList==null?1:0,pageRequest);
        List<Attachment> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        response.setData(list);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/listApproved/{departmentId}")
    ResponseEntity<List<Attachment>> getListApprovedByDepartment(@PathVariable String departmentId){
        Long _id =Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(departmentId)));
        List<Attachment> list = repository.findByDepartmentIdAndStatusOrderByOrderNo(_id,AttachmentController.STATUS_APPROVED);
        return ResponseEntity.ok(list);
    }
    @PostMapping(value = "/listApprovedByDepartment/{departmentId}")
    ResponseEntity<List<Attachment>> getListApprovedByDepartment(@PathVariable Long departmentId){
        List<Attachment> list = repository.findByDepartmentIdAndStatusOrderByOrderNo(departmentId,AttachmentController.STATUS_APPROVED);
        return ResponseEntity.ok(list);
    }

    private AttachmentDto attachmentToDto(Attachment attachment){
        SqlParameterSource parameter =  new MapSqlParameterSource()
                .addValue("_id",attachment.getId());
        return namedParameterJdbcTemplate.queryForObject("select * from (select atta.id,atta.name,atta.version,atta.department_id,atta.issue_date" +
                        " ,atta.uuid,atta.order_no,atta.status,d.name department_name,d.department_no,coalesce(OCTET_LENGTH(atta.pdf_file_data),0) pdf_file_size" +
                        " ,coalesce(OCTET_LENGTH(atta.temp_file_data),0) temp_file_size  from attachment atta inner join department d on atta.department_id = d.id" +
                        " where atta.id = :_id) tab"
                ,parameter,  BeanPropertyRowMapper.newInstance(AttachmentDto.class));
    }

    @CrossOrigin
    @Transactional
    @PostMapping(value = "/updateStatus/{encId}")
    ResponseEntity<?> updateStatus(@PathVariable String encId,@RequestParam Integer nextStatus){
            Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
            repository.updateStatus(_id,nextStatus);
            Attachment atta = repository.findById(_id).orElse(null);
            if(atta != null){
                addTransaction(atta);
                return ResponseEntity.ok(attachmentToDto(atta));
            }else{
                return ResponseEntity.badRequest().build();
            }
    }

    @Autowired
    WorkGuideRepository workGuideRepository;

    @CrossOrigin
    @Transactional
    @PostMapping(value = "/replaceAttachment/{sEncId}/{dEncId}")
    ResponseEntity<?> replaceAttachment(@PathVariable String sEncId,@PathVariable String dEncId) throws Exception {
        HashMap<String, Object> m = new HashMap<>();
        Long s_id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(sEncId)));
        Long d_id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(dEncId)));
        Attachment sAtta = repository.getOne(s_id);
        Attachment dAtta = repository.getOne(d_id);
        WorkGuide wg = workGuideRepository.findWorkGuideByAttachmentId(d_id);
        if(wg != null) {
            List<WorkGuideProcedure> procList = wg.getProcedures();
            for (WorkGuideProcedure proc : procList) {
                List<ProcedureStep> stepList = proc.getSteps();
                for (ProcedureStep step : stepList) {
                    List<Attachment> fileList = step.getFiles();
                    if (fileList.contains(dAtta)) {
                        fileList.remove(dAtta);
                        fileList.add(sAtta);
                    }
                }
            }
            workGuideRepository.save(wg);
        }else{
            throw new Exception("WorkGuide not found");
        }
        m.put("sid",s_id);
        m.put("did",d_id);
        return ResponseEntity.ok(m);

    }
    @PostMapping(value = "/search2")
    ResponseEntity<?> search2(@Valid @RequestBody DataTableRequest<GenericData> request,
                             @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
        String str_id = request.getData().getData().get("departmentId").toString();
        List<Long> departmentList = null;
        if(!str_id.equals("-1")){
            try {
                Long dep_id = Long.parseLong(Objects.requireNonNull(
                        SystemUtil.decrypt(request.getData().getData().get("departmentId").toString())));
                departmentList = departmentRepository.allChild(dep_id);
            }catch (Exception ex){
                return ResponseEntity.badRequest().build();
            }
        }

        Integer status_id = Integer.parseInt(request.getData().getData().get("statusId").toString());
        String sortBy = request.getSortBy();
        sortBy = sortBy.replaceAll("([^_A-Z])([A-Z])", "$1_$2");
        sortBy =  sortBy.length()==0?"":" order by " + sortBy+ (request.getSortDesc()?" desc ":"");
        SqlParameterSource parameter =  new MapSqlParameterSource()
                .addValue("status_id",status_id)
                .addValue("departmentList",departmentList)
                .addValue("all_department",departmentList==null?1:0)
                .addValue("currentPage",pageRequest.getPageNumber()*pageRequest.getPageSize())
                .addValue("pageSize",pageRequest.getPageSize());

        List<AttachmentDto> lst = namedParameterJdbcTemplate.query("select * from (select atta.id,atta.name,atta.version,atta.department_id,atta.issue_date" +
                " ,atta.uuid,atta.order_no,atta.status,d.name department_name,d.department_no,coalesce(OCTET_LENGTH(atta.pdf_file_data),0) pdf_file_size" +
                " ,coalesce(OCTET_LENGTH(atta.temp_file_data),0) temp_file_size  from attachment atta inner join department d on atta.department_id = d.id" +
                " where (:status_id = -1 or atta.status = :status_id)"+
                " and (:all_department=1 or atta.department_id in (:departmentList))) tab" +
                sortBy+
                " limit :pageSize offset :currentPage " ,parameter,  BeanPropertyRowMapper.newInstance(AttachmentDto.class));
        Long cnt = namedParameterJdbcTemplate.queryForObject("select count(*)  from attachment atta inner join department d on atta.department_id = d.id" +
                " where (:status_id = -1 or atta.status = :status_id)" +
                " and (:all_department=1 or atta.department_id in (:departmentList))",parameter,Long.class);
        DataTableResponse response = new DataTableResponse();
      //  Page<Attachment> page = repository.search(departmentList,status_id,departmentList==null?1:0,pageRequest);
        //List<Attachment> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(cnt);
        response.setData(lst);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/deleteFile/{encId}")
    ResponseEntity<?> deleteAttachment(@PathVariable String encId){
        Map<String, Object> m = new HashMap<>(1);
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
        Attachment atta = repository.findById(_id).orElse(null);
        if(atta != null && atta.getStatus() == STATUS_NEW){
            repository.delete(atta);
            m.put("success",true);
            m.put("object",atta);
            m.put("message",1);
            return ResponseEntity.ok(m);
        }else if(atta != null){
            int cnt = procedureStepRepository.countAttachmentUsed(atta.getId());
            if(cnt == 0){
                repository.delete(atta);
                m.put("success",true);
                m.put("object",atta);
                m.put("message",1);
                return ResponseEntity.ok(m);
            }else{
                m.put("success",false);
                m.put("message",2);
                return ResponseEntity.ok(m);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/getUsedCount/{encId}")
    ResponseEntity<?> getUsedCount(@PathVariable String encId){
        Map<String, Object> m = new HashMap<>(1);
        try {
            Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
            int cnt = procedureStepRepository.countAttachmentUsed(_id);
            m.put("count",cnt);
            m.put("encId",encId);
            return ResponseEntity.ok(m);
        }catch (Exception ex){
            return ResponseEntity.badRequest().build();
        }

    }

    private void addTransaction(Attachment attachment){
        AttachmentTransaction transaction = new AttachmentTransaction();
        transaction.setActionType(attachment.getStatus());
        transaction.setAttachmentId(attachment.getId());
        attachmentTransactionRepository.save(transaction);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping(value = "/listTransactionByAttachment/{encId}")
    public ResponseEntity<List<AttachmentTransactionDto>> listTransactionByAttachment(@PathVariable String encId){
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
        List<AttachmentTransactionDto> list = jdbcTemplate.query("select at.remarks ,at.action_type,at.created_at,e.full_name ,e.id " +
                " from attachment_transaction at " +
                " inner join employee e on at.created_by = e.id " +
                "where at.attachment_id = ? order by at.id desc",new AttachmentTransactionMapper(), _id);
        return ResponseEntity.ok(list);
    }

}
