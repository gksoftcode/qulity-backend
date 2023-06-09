package com.wisecode.core.controller;

import com.wisecode.core.RoleName;
import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.dto.AuditPlanTransactionDto;
import com.wisecode.core.entities.*;
import com.wisecode.core.mapper.AuditPlanTransactionMapper;
import com.wisecode.core.payload.*;
import com.wisecode.core.repositories.*;
import com.wisecode.core.util.ApplicationUtil;
import com.wisecode.core.util.SystemUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/audit_plan")
@Log4j2
public class AuditPlanController extends GenericController<AuditPlan>{

    public static final int STATUS_NEW = 0;
    public static final int STATUS_REJECTED = 1;
    public static final int STATUS_APPROVED = 10;
    public static final int STATUS_RESULT_REJECTED = 11;
    public static final int STATUS_RESULT_APPROVED = 20;

    public static final int ITEM_NEW = 0;
    public static final int ITEM_EDITED = 5;
    public static final int ITEM_EVALUATED = 10;

    public static final int CORRECTIVE_ACTION_STATUS_NEW = 0;
    public static final int CORRECTIVE_ACTION_STATUS_IMPLEMENTED = 1;
    public static final int CORRECTIVE_ACTION_STATUS_NOT_IMPLEMENTED = 2;
    public static final int CORRECTIVE_ACTION_APPROVED_NEW = 0;
    public static final int CORRECTIVE_ACTION_APPROVED_YES = 1;
    public static final int CORRECTIVE_ACTION_APPROVED_NO = 2;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ApplicationUtil applicationUtil;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    CorrectiveActionRepository correctiveActionRepository;

    @Autowired
    AuditPlanTransactionRepository auditPlanTransactionRepository;
    @Autowired
    CheckListItemRepository checkListItemRepository;
    AuditPlanRepository repository;
    @Autowired
    CheckListItemTransactionRepository checkListItemTransactionRepository;

   @Autowired
   public AuditPlanController(AuditPlanRepository repository,CheckListItemRepository checkListItemRepository){
       super(repository);
       this.repository = repository;
       this.checkListItemRepository = checkListItemRepository;
   }

    @PostMapping(value = "/saveData/{id}")
    ResponseEntity<AuditPlan> save(@RequestBody AuditPlan json, @PathVariable String id) {
       if(id!=null && id.equals("-1")){
           AuditPlan entity = repository.save(json);
           AuditPlanTransaction apt = new AuditPlanTransaction();
           apt.setActionType(0);
           apt.setRemarks("");
           apt.setAuditPlanId(entity.getId());
           auditPlanTransactionRepository.save(apt);
           return ResponseEntity.ok(entity);
       }
        try {
            long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
            AuditPlan entity = repository.findById(_id).orElse(null);
            if (entity != null) {
                BeanUtils.copyProperties(json, entity);
                repository.save(entity);
                return ResponseEntity.ok(entity);
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    private void addItemTransaction(Long itemId,Integer actionType){
       CheckListItemTransaction transaction = new CheckListItemTransaction();
       transaction.setActionType(actionType);
       transaction.setItemId(itemId);
        checkListItemTransactionRepository.save(transaction);
    }
    @PostMapping(value = "/saveItem/{id}")
    ResponseEntity<CheckListItem> saveItem(@RequestBody CheckListItem json, @PathVariable String id) {
        if(id!=null && id.equals("-1")){
            CheckListItem entity = checkListItemRepository.save(json);
            addItemTransaction(entity.getId(),ITEM_NEW);
            return ResponseEntity.ok(entity);
        }
        try {
            long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
            CheckListItem entity = checkListItemRepository.findById(_id).orElse(null);
            if (entity != null) {
                BeanUtils.copyProperties(json, entity);
                checkListItemRepository.save(entity);
                addItemTransaction(entity.getId(),ITEM_EDITED);
                return ResponseEntity.ok(entity);
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }
    @PostMapping(value = "/updateResultItem/{id}")
    public ResponseEntity<CheckListItem> updateResultItem(@RequestBody CheckListItem json, @PathVariable String id){
        try {
            long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
            CheckListItem entity = checkListItemRepository.findById(_id).orElse(null);
            if (entity != null) {
                entity.setAuditResult(json.getAuditResult());
                entity.setRemarks(json.getRemarks());
                entity.setEmployeeId(json.getEmployeeId());
                checkListItemRepository.save(entity);
                addItemTransaction(entity.getId(),ITEM_EVALUATED);
                return ResponseEntity.ok(entity);
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/deleteItem/{id}")
    ResponseEntity<Map<String, Object>> deleteItem(@RequestBody CheckListItem json, @PathVariable String id) {
        try {
            long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
            checkListItemRepository.deleteById(_id);
            Map<String, Object> m = new HashMap<>();
            m.put("success", true);
            m.put("id", _id);
            return ResponseEntity.ok(m);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/listCheckListItem/{encId}")
    public ResponseEntity<List<CheckListItem>> listCheckListItem(@PathVariable String encId){
       try{
          Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
          List<CheckListItem> list = checkListItemRepository.findByAuditPlanIdOrderById(_id);
          return ResponseEntity.ok(list);
       }catch(Exception ex){
           return ResponseEntity.badRequest().build();
       }
    }



    @PostMapping("/search")
    public ResponseEntity<DataTableResponse> search(@Valid @RequestBody DataTableRequest<GenericData> request,
                                                    @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
        String year_id = request.getData().getData().get("yearId")==null?null:request.getData().getData().get("yearId").toString();
        String dep_id = request.getData().getData().get("depId")==null?null:request.getData().getData().get("depId").toString();
        DataTableResponse response = new DataTableResponse();
        boolean fullAccess = false;
        boolean sectionManger = false;
        for (Role role:currentUser.getRoles()){
            if(role.getName().equals(RoleName.ROLE_MANAGER)||
                    role.getName().equals(RoleName.ROLE_QUALITY)||
                    role.getName().equals(RoleName.ROLE_ADMIN)){
                fullAccess = true;
                break;
            }
            if(role.getName().equals(RoleName.ROLE_SECTION_MANAGER)){
                sectionManger = true;
            }
        }
        int fullSearch = 1;
        Integer yearId = year_id == null ? null : Integer.parseInt(year_id);
        long depId = dep_id == null ? -1L : Long.parseLong(dep_id);
        List<Long> departmentList = new ArrayList<>();
        departmentList.add(-1L);
        if(!fullAccess && !sectionManger && depId != -1){
            Employee emp = employeeRepository.findById(currentUser.getId()).get();
            sectionManger = applicationUtil.isManager(emp) && (emp.getDepartmentId().equals(depId)) ;
        }
        if(depId != -1 && (fullAccess || sectionManger)){
            fullSearch = 0;
            try {
                departmentList = departmentRepository.allChild(depId);
            }catch (Exception ex){
                return ResponseEntity.badRequest().build();
            }
        }

        Page<AuditPlan> page =fullAccess?repository.searchAdmin(yearId,fullSearch,departmentList,pageRequest) :
                repository.search(yearId,currentUser.getId(),departmentList,pageRequest);
        List<AuditPlan> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        response.setData(list);
        return ResponseEntity.ok().body(response);
    }
    @Transactional
    @PostMapping(value = "/updateStatus/{encId}/{newStatus}")
    public ResponseEntity<Map<String, Object>> changeStatus(@RequestBody PairData<String,String> remarks,
                                                            @PathVariable String encId, @PathVariable String newStatus,
                                                            @CurrentUser User user) {
        try {
                Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
                AuditPlanTransaction apt = new AuditPlanTransaction();
                apt.setActionType(Integer.parseInt(newStatus));
                apt.setRemarks(remarks.getValue());
                apt.setAuditPlanId(_id);
                auditPlanTransactionRepository.save(apt);
                    repository.updateStatus(_id,Integer.parseInt(newStatus));

            AuditPlan ap = repository.findById(_id).orElse(null);
            Map<String, Object> m = new HashMap<>();
            m.put("success", true);
            m.put("id", _id);
            m.put("object", ap);
            return ResponseEntity.ok(m);
        }catch(Exception ex){
            Map<String, Object> m = new HashMap<>();
            m.put("success", false);
            return ResponseEntity.ok(m);
        }
    }

    @PostMapping(value = "/listTransactionByAuditPlan/{encId}")
    public ResponseEntity<List<AuditPlanTransactionDto>> listTransactionByAuditPlan(@PathVariable String encId){
           Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
           List<AuditPlanTransactionDto> list = jdbcTemplate.query("select apt.remarks ,apt.action_type,apt.created_at,e.full_name ,e.id " +
                   " from audit_plan_transaction apt " +
                   " inner join employee e on apt.created_by = e.id " +
                   "where apt.audit_plan_id = ? order by apt.id desc",new AuditPlanTransactionMapper(), _id);
           return ResponseEntity.ok(list);

    }

    @PostMapping(value = "/getCorrectiveAction/{auditItemId}")
    public ResponseEntity<CorrectiveAction> getCorrectiveActionByAuditItemId(@PathVariable Long auditItemId){
       CorrectiveAction correctiveAction = correctiveActionRepository.findByAuditItemId(auditItemId);
       if(correctiveAction != null){
           return ResponseEntity.ok(correctiveAction);
       }else{
           return ResponseEntity.badRequest().build();
       }
    }
    @PostMapping(value = "/saveCorrectiveAction/{id}")
    public ResponseEntity<CorrectiveAction> saveCorrectiveAction(@RequestBody CorrectiveAction json, @PathVariable String id) throws Exception {
        if(id!=null && id.equals("-1")){
            CorrectiveAction entity = correctiveActionRepository.save(json);
            return ResponseEntity.ok(entity);
        }
        long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
        CorrectiveAction entity = correctiveActionRepository.findById(_id).orElse(null);
        if (entity != null) {
            BeanUtils.copyProperties(json, entity);
            correctiveActionRepository.save(entity);
            return ResponseEntity.ok(entity);
        }
        throw new Exception();
    }
    @PostMapping(value = "/deleteCorrectiveAction/{id}")
    public void deleteCorrectiveAction(@RequestBody CorrectiveAction json, @PathVariable String id) throws Exception {

        long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(id)));
        correctiveActionRepository.deleteById(_id);
    }

    @Transactional
    @PostMapping(value = "/updateCorrectionFollow/{encId}/{newStatus}")
    public ResponseEntity<CorrectiveAction> updateFollow(@RequestBody PairData<String,String> remarks,@PathVariable String encId,
                                                            @PathVariable Integer newStatus,
                                                            @CurrentUser User user) throws Exception {
            Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
            correctiveActionRepository.updateFollow(newStatus,user.getId(),new Date(),_id,remarks.getValue());
           CorrectiveAction ca = correctiveActionRepository.findById(_id).orElse(null);
           if(ca != null){
               return ResponseEntity.ok(ca);
           }else {
               throw new Exception("Correction Action not found");
           }
    }
    @Transactional
    @PostMapping(value = "/updateCorrectionApproved/{encId}/{newStatus}")
    public ResponseEntity<CorrectiveAction> updateApproved(@PathVariable String encId,
                                                         @PathVariable Integer newStatus,
                                                         @CurrentUser User user) throws Exception {
        Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
        correctiveActionRepository.updateApproved(newStatus,user.getId(),new Date(),_id);
        CorrectiveAction ca = correctiveActionRepository.findById(_id).orElse(null);
        if(ca != null){
            return ResponseEntity.ok(ca);
        }else {
            throw new Exception("Correction Action not found");
        }
    }

    @PostMapping("/listCorrectionByAudit")
    public ResponseEntity<DataTableResponse> listCorrectionByAudit(@Valid @RequestBody DataTableRequest<GenericData> request,
                                                                @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
        String auditId = request.getData().getData().get("auditId")==null?null:request.getData().getData().get("auditId").toString();
        DataTableResponse response = new DataTableResponse();
        Page<CorrectiveAction> page = correctiveActionRepository.findAllByAuditId(Long.parseLong(auditId),pageRequest);
        List<CorrectiveAction> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        response.setData(list);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/listCorrectionByReport")
    public ResponseEntity<DataTableResponse> listCorrectionByReport(@Valid @RequestBody DataTableRequest<GenericData> request,
                                                                   @CurrentUser User currentUser){
        PageRequest pageRequest = preparePageRequest(request);
        String _id = request.getData().getData().get("reportId")==null?null:request.getData().getData().get("reportId").toString();
        DataTableResponse response = new DataTableResponse();
        Page<CorrectiveAction> page = correctiveActionRepository.findAllByReportId(Long.parseLong(_id),pageRequest);
        List<CorrectiveAction> list =page.getContent();
        response.setCurrentPage(request.getCurrentPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        response.setData(list);
        return ResponseEntity.ok().body(response);
    }

}
