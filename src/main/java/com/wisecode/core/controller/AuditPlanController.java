package com.wisecode.core.controller;

import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.dto.AuditPlanTransactionDto;
import com.wisecode.core.entities.*;
import com.wisecode.core.mapper.AuditPlanTransactionMapper;
import com.wisecode.core.payload.DataTableRequest;
import com.wisecode.core.payload.DataTableResponse;
import com.wisecode.core.payload.GenericData;
import com.wisecode.core.payload.PairData;
import com.wisecode.core.repositories.AuditPlanRepository;
import com.wisecode.core.repositories.AuditPlanTransactionRepository;
import com.wisecode.core.repositories.CheckListItemRepository;
import com.wisecode.core.repositories.CheckListItemTransactionRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Autowired
    JdbcTemplate jdbcTemplate;

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
        String year_id = request.getData().getData().get("yearId").toString();
        DataTableResponse response = new DataTableResponse();
        Page<AuditPlan> page = repository.search(Integer.parseInt(year_id),pageRequest);
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
       try {
           Long _id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(encId)));
           List<AuditPlanTransactionDto> list = jdbcTemplate.query("select apt.remarks ,apt.action_type,apt.created_at,e.full_name ,e.id " +
                   " from audit_plan_transaction apt " +
                   " inner join employee e on apt.created_by = e.id " +
                   "where apt.audit_plan_id = ? order by apt.id desc",new AuditPlanTransactionMapper(), _id);
           return ResponseEntity.ok(list);
       }
       catch (Exception eee){
          return ResponseEntity.badRequest().build();
       }
    }
}
