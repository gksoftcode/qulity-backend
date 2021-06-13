package com.wisecode.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class WorkGuideTransaction extends UserDateAudit implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "work_guide_id")
    Long workGuideId;

    @Column(name = "remarks",length = 1000)
    String remarks;

    Integer actionType;

//    @JsonIgnoreProperties({"shortName","dob","gender","job","department"})
//    @ManyToOne
//    @JoinColumn(name = "created_by",updatable = false,insertable = false)
//    Employee employee;

    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
