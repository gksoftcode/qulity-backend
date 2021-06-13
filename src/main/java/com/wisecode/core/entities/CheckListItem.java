package com.wisecode.core.entities;

import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.Getter;
import lombok.Setter;

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
