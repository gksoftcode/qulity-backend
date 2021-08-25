package com.wisecode.core.controller;

import com.wisecode.core.RoleName;
import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.dto.WorkGuideDto;
import com.wisecode.core.dto.WorkGuideTransactionDto;
import com.wisecode.core.entities.*;
import com.wisecode.core.mapper.WorkGuideTransactionMapper;
import com.wisecode.core.payload.*;
import com.wisecode.core.repositories.DepartmentRepository;
import com.wisecode.core.repositories.EmployeeRepository;
import com.wisecode.core.repositories.WorkGuideRepository;
import com.wisecode.core.repositories.WorkGuideTransactionRepository;
import com.wisecode.core.util.ApplicationUtil;
import com.wisecode.core.util.SystemUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/work_guide")
@Log4j2
public class WorkGuideController extends GenericController<WorkGuide>{

    public static final int TYPE_NEW= 1;
    public static final int TYPE_NEW_VERSION = 5;
    public static final int TYPE_PRODUCTION = 10;
    public static final int TYPE_VERSIONED= 20;
    public static final int STATUS_NEW = 0;
    public static final int STATUS_NEW_VERSION = 1;
    public static final int STATUS_SUBMITTED = 10;
    public static final int STATUS_SECTION_REJECTED = 15;
    public static final int STATUS_SECTION_APPROVAL = 20;
    public static final int STATUS_QUALITY_REJECTED = 25;
    public static final int STATUS_QUALITY_APPROVAL = 30;
    public static final int STATUS_FINAL_APPROVAL = 40;

    WorkGuideRepository repository;
    @Autowired
    WorkGuideTransactionRepository wgtRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    ApplicationUtil appUtil;

    @Autowired
    NamedParameterJdbcTemplate template;

    //@Secured({"ROLE_ADMIN","ROLE_QUALITY","ROLE_MANAGER","ROLE_SECTION_ENTRY","ROLE_SECTION_MANAGER"})
    //@PreAuthorize("principal.username == 'fajer11'")
    @PostMapping("/search")
    public ResponseEntity<?> getList(@Valid @RequestBody DataTableRequest<GenericData> request,
                                     @CurrentUser User currentUser){
        if(request.getSortBy()!= null && request.getSortBy().equals("departmentName")){
            request.setSortBy("department.name");
        }
        if(request.getSortBy()!= null && request.getSortBy().equals("departmentNo")){
            request.setSortBy("department.departmentNo");
        }
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
        Integer type_id = Integer.parseInt(request.getData().getData().get("typeId").toString());
        Integer status_id = Integer.parseInt(request.getData().getData().get("statusId").toString());
        DataTableResponse response = new DataTableResponse();
        Page<WorkGuideDto> page = repository.search2(type_id,status_id,departmentList,departmentList==null?1:0,pageRequest);
        List<WorkGuideDto> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        response.setData(list);
        return ResponseEntity.ok().body(response);
    }
    public WorkGuideController(WorkGuideRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @PostMapping(value = "/saveWithFile/{id}")
    ResponseEntity<WorkGuide> saveWithFile(@Validated @RequestBody WorkGuide json, @PathVariable String id) {
        if (id.equals("-1")) {
//            if(json.getFileList() != null){
//                if(json.getFiles() == null) {
//                    json.setFiles(new ArrayList<>());
//                }
//                for (FileUploadDtls file : json.getFileList()){
//                    try {
//                        Attachment attachment = new Attachment();
//                        byte[] data = Files.readAllBytes(Paths.get(file.getFilePath()));
//                        attachment.setFileData(data);
//                        attachment.setName(file.getName());
//                        attachment.setVersion(1);
//                        attachment.setFileName(file.getOriginalFileName());
//                        attachment.setMimeType(file.getFileType());
//                        json.getFiles().add(attachment);
//                        for (WorkGuideProcedure proc : json.getProcedures()){
//                            for (ProcedureStep step :proc.getSteps()){
//                                if(step.getFiles() == null){
//                                    step.setFiles(new ArrayList<>());
//                                }
//                                if(step.getFileList()!=null){
//                                    if(step.getFileList().contains(file)){
//                                        step.getFiles().add(attachment);
//                                    }
//                                }
//                            }
//                        }
//                    }catch(Exception ignored){}
//                }
//            }
            repository.save(json);
            return ResponseEntity.ok(json);
        } else {
            long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
            WorkGuide entity = repository.findById(_id).orElse(null);
//                if(json.getFileList() != null){
//                    if(json.getFiles() == null) {
//                        json.setFiles(new ArrayList<>());
//                    }
//                    for (FileUploadDtls file : json.getFileList()){
//                        try {
//                            Attachment attachment = new Attachment();
//                            byte[] data = Files.readAllBytes(Paths.get(file.getFilePath()));
//                            attachment.setFileData(data);
//                            attachment.setName(file.getName());
//                            attachment.setVersion(1);
//                            attachment.setUuid(UUID.randomUUID().toString());
//                            attachment.setFileName(file.getOriginalFileName());
//                            attachment.setMimeType(file.getFileType());
//                            json.getFiles().add(attachment);
//                            for (WorkGuideProcedure proc : json.getProcedures()){
//                                for (ProcedureStep step :proc.getSteps()){
//                                    if(step.getFiles() == null){
//                                        step.setFiles(new ArrayList<>());
//                                    }
//                                    if(step.getFileList()!=null){
//                                        if(step.getFileList().contains(file)){
//                                            step.getFiles().add(attachment);
//                                        }
//                                    }
//                                }
//                            }
//                        }catch(Exception ignored){}
//                    }
//                }
            if (entity != null) {
                BeanUtils.copyProperties(json, entity);
                repository.save(entity);
                return ResponseEntity.ok(entity);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/getByDepartment/{departmentId}/{type}")
    ResponseEntity<WorkGuide> getByDepartment(@PathVariable String departmentId,@PathVariable Integer type){
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(departmentId)));
        WorkGuide object = repository.findByDepartmentIdAndType(_id,type);
        if(object == null && type == TYPE_NEW){
            object = new WorkGuide();
            object.setDepartmentId(_id);
            object.setVersion(1);
            object.setType(1);
            object = repository.save(object);
            WorkGuideTransaction wgt = new WorkGuideTransaction();
            wgt.setActionType(STATUS_NEW);
            wgt.setWorkGuideId(object.getId());
            wgtRepository.save(wgt);
        }
        if(type == TYPE_NEW_VERSION){
            WorkGuide ob = repository.findByDepartmentIdAndType(_id,TYPE_NEW_VERSION);
            if(ob == null){
                WorkGuide temp = repository.findByDepartmentIdAndType(_id,TYPE_PRODUCTION);
                object  = createNewVersion(temp.getId());
                WorkGuideTransaction wgt = new WorkGuideTransaction();
                wgt.setActionType(STATUS_NEW);
                wgt.setWorkGuideId(object.getId());
                wgtRepository.save(wgt);
            }else{
                object = ob;
            }

        }
        return ResponseEntity.ok(object);
    }

    @Transactional
    @PostMapping(value = "/updateStatus/{encId}/{newStatus}")
    public ResponseEntity<Map<String, Object>> changeStatus(@RequestBody PairData<String,String> remarks,
                                                            @PathVariable String encId, @PathVariable String newStatus,
                                                            @CurrentUser User user) {
        try {
            Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
            int count = repository.updateStatus(_id, Integer.parseInt(newStatus));
            if(Integer.parseInt(newStatus) == STATUS_FINAL_APPROVAL) {
                repository.updateType(_id,TYPE_PRODUCTION);
            }
            if (count > 0) {
                WorkGuideTransaction wgt = new WorkGuideTransaction();
                wgt.setActionType(Integer.parseInt(newStatus));
                wgt.setRemarks(remarks.getValue());
                wgt.setWorkGuideId(_id);
                wgtRepository.save(wgt);
                if(Integer.parseInt(newStatus) == STATUS_FINAL_APPROVAL) {
                    WorkGuide wg = repository.findById(_id).orElse(null);
                    assert wg != null;
                    repository.updateOldType(_id, wg.getDepartmentId());
                }
            }
            Map<String, Object> m = new HashMap<>();
            m.put("success", true);
            m.put("id", _id);
            m.put("count", count);
            return ResponseEntity.ok(m);
        }catch(Exception ex){
            Map<String, Object> m = new HashMap<>();
            m.put("success", false);
            return ResponseEntity.ok(m);
        }

    }

    @PostMapping(value = "/getPermission/{encId}")
    public ResponseEntity<Map<String, Object>> getPermissionById( @PathVariable String encId, @CurrentUser User user){
        Map<String, Object> map = new HashMap<>();
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", _id);
        Map<String, Object> result =template.queryForMap("select status,work_guide_type,department_id from work_guide where id = :id"
                ,namedParameters);
        int status =Integer.parseInt(result.get("status").toString());
        int type =Integer.parseInt(result.get("work_guide_type").toString());
        long departmentId=Long.parseLong(result.get("department_id").toString());
        Department department = departmentRepository.findById(departmentId).get();
        if(type == TYPE_NEW || type == TYPE_NEW_VERSION){
            Employee employee = employeeRepository.findById(user.getId()).get();

            if((status ==STATUS_SUBMITTED || status == STATUS_QUALITY_REJECTED )&&
                    ( (appUtil.hasPermission(employee.getUser(), RoleName.ROLE_SECTION_MANAGER)
                    && departmentId == employee.getDepartmentId() )
                    ||(departmentId == employee.getDepartmentId() && appUtil.isManager(employee))
                    || appUtil.isSuperManager(employee,department))
            )
            {
                map.put("HAS_STEP_2",1); //Section manager action
            }
            if(status == STATUS_SECTION_APPROVAL && appUtil.hasPermission(employee.getUser(), RoleName.ROLE_QUALITY))
            {
                map.put("HAS_STEP_3",1); // Quality department Action
            }
            if((status == STATUS_NEW || status == STATUS_SECTION_REJECTED)  &&
                    appUtil.hasPermission(employee.getUser(), RoleName.ROLE_SECTION_ENTRY) &&
                    departmentId == employee.getDepartmentId())
            {
                map.put("HAS_STEP_1",1);//employee send WG
            }
            if(status == STATUS_QUALITY_APPROVAL  &&
                    appUtil.hasPermission(employee.getUser(), RoleName.ROLE_MANAGER))
            {
                map.put("HAS_STEP_4",1); // Manager Action
            }
        }
        return ResponseEntity.ok(map);
    }

    @PostMapping(value = "/canDuplicate/{departmentEncId}")
    public ResponseEntity<Map<String, Object>> canDuplicate(@PathVariable String departmentEncId,@CurrentUser User user){
        Map<String, Object> map = new HashMap<>();
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(departmentEncId)));
        WorkGuide production = repository.hasProduction(_id);
        int _new = repository.hasDuplicateOrNew(_id);
        if(production != null && _new == 0){
            WorkGuide duplicate = createNewVersion(production.getId());
            WorkGuideTransaction wgt = new WorkGuideTransaction();
            wgt.setActionType(STATUS_NEW_VERSION);
            wgt.setRemarks("");
            wgt.setWorkGuideId(duplicate.getId());
            wgtRepository.save(wgt);
            map.put("result","success");
            map.put("object",duplicate);
        }else{
            map.put("result", "error");
        }
        return ResponseEntity.ok(map);
    }
    @PostMapping(value = "/canCreateNew/{departmentEncId}")
    public ResponseEntity<Map<String, Object>> canCreateNew(@PathVariable String departmentEncId,@CurrentUser User user){
        Map<String, Object> map = new HashMap<>();
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(departmentEncId)));
        WorkGuide production = repository.hasProduction(_id);
        int new_count = repository.hasDuplicateOrNew(_id);
        if(production == null && new_count == 0){
            WorkGuide _new = new WorkGuide();
            _new.setVersion(1);
            _new.setType(TYPE_NEW);
            _new.setDepartmentId(_id);
            _new.setIssueDate(new Date());
            _new.setStatus(STATUS_NEW);
            _new = repository.save(_new);
            WorkGuideTransaction wgt = new WorkGuideTransaction();
            wgt.setActionType(STATUS_NEW);
            wgt.setRemarks("");
            wgt.setWorkGuideId(_new.getId());
            wgtRepository.save(wgt);
            map.put("result","success");
            map.put("object",_new);
        }else{
            map.put("result", "error");
        }
        return ResponseEntity.ok(map);
    }
    private WorkGuide createNewVersion(Long id){
        WorkGuide wg = repository.findById(id).orElse(null);
        WorkGuide temp = new WorkGuide();
        if(wg != null){
            BeanUtils.copyProperties(wg,temp);
            temp.setId(-1L);
            temp.setType(TYPE_NEW_VERSION);
            temp.setStatus(STATUS_NEW);
            temp.setVersion(wg.getVersion()==null?1:wg.getVersion()+1);
            temp.setDefinitions(new ArrayList<>());
            for (WorkGuideDefinition def : wg.getDefinitions()){
                WorkGuideDefinition tt = new WorkGuideDefinition();
                BeanUtils.copyProperties(def,tt);
                tt.setId(-1L);
                temp.getDefinitions().add(tt);
            }
            temp.setResponsibilities(new ArrayList<>());
            for (WorkGuideResponsibility resp : wg.getResponsibilities()){
                WorkGuideResponsibility tt = new WorkGuideResponsibility();
                BeanUtils.copyProperties(resp,tt);
                tt.setId(-1L);
                temp.getResponsibilities().add(tt);
            }
            temp.setPointers(new ArrayList<>());
            for (WorkGuidePointer ptr : wg.getPointers()){
                WorkGuidePointer tt = new WorkGuidePointer();
                BeanUtils.copyProperties(ptr,tt);
                tt.setId(-1L);
                temp.getPointers().add(tt);
            }
            temp.setReferences(new ArrayList<>());
            for (WorkGuideReference ref : wg.getReferences()){
                WorkGuideReference tt = new WorkGuideReference();
                BeanUtils.copyProperties(ref,tt);
                tt.setId(-1L);
                temp.getReferences().add(tt);
            }
            temp.setProcedures(new ArrayList<>());
            for(WorkGuideProcedure proc : wg.getProcedures()){
                WorkGuideProcedure tt = new WorkGuideProcedure();
                BeanUtils.copyProperties(proc,tt);
                tt.setId(-1L);
                tt.setSteps(new ArrayList<>());
                for (ProcedureStep step : proc.getSteps()){
                    ProcedureStep ttt = new ProcedureStep();
                    BeanUtils.copyProperties(step,ttt);
                    ttt.setId(-1L);
                    ttt.setFiles(new ArrayList<>());
                    for(Attachment atta :step.getFiles()){
                        ttt.getFiles().add(atta);
                    }
                    tt.getSteps().add(ttt);
                }
                temp.getProcedures().add(tt);
            }
            temp = repository.save(temp);
            return temp;
        }
        return null;
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping(value = "/listTransactionByWorkGuide/{encId}")
    public ResponseEntity<List<WorkGuideTransactionDto>> listTransactionByAuditPlan(@PathVariable String encId){
            Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
            List<WorkGuideTransactionDto> list = jdbcTemplate.query("select wgt.remarks ,wgt.action_type,wgt.created_at,e.full_name ,e.id " +
                    " from work_guide_transaction wgt " +
                    " inner join employee e on wgt.created_by = e.id " +
                    "where wgt.work_guide_id = ? order by wgt.id desc",new WorkGuideTransactionMapper(), _id);
            return ResponseEntity.ok(list);
    }
}
