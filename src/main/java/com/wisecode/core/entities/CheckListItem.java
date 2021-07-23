package com.wisecode.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "check_list_item")
@Getter
@Setter
public class CheckListItem  extends UserDateAudit implements BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    String content;
    
    @ManyToOne
    @JoinColumn(name = "audit_plan_id",insertable = false,updatable = false)
    AuditPlan auditPlan;

    @Column(name = "audit_plan_id")
    Long auditPlanId;

    Integer auditResult;

    @Column(name = "employee_id")
    Long employeeId;

    @JsonIgnoreProperties({"department","color","dob","gender","deleted","jobId",
            "mobileNumber","user","shortName","createdAt","updatedAt","createdBy","updatedBy"})
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    Employee employee;

    @Lob
    String remarks;
    
    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
