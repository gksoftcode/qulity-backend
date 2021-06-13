package com.wisecode.core.entities;

import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class AuditPlanTransaction extends UserDateAudit implements BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "audit_plan_id")
    Long auditPlanId;

    @Column(name = "remarks",length = 1000)
    String remarks;

    Integer actionType;

    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
