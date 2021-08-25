package com.wisecode.core.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "audit_plan")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"},callSuper = false)
public class AuditPlan extends UserDateAudit implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date toDate;

    Integer status;

    @JsonIgnoreProperties({"createdAt","updatedAt","createdBy","updatedBy","department","user"})
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.REFRESH,CascadeType.REMOVE},fetch = FetchType.LAZY)
    List<Employee> auditors;


    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_plan_id")
    List<AuditPlanExtraData> extraList;

    @JsonIgnoreProperties({"departmentList","createdBy","updatedBy","createdAt","updatedAt","manager","parentDepartment"})
    @ManyToOne
    @JoinColumn(name = "department_id" ,insertable = false,updatable = false)
    Department department;

    @Column(name = "department_id")
    Long departmentId;

    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
