package com.wisecode.core.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "audit_final_report")
@Getter
@Setter
public class AuditFinalReport extends UserDateAudit implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String title;

    String domain;

    Integer reportNo;

    Integer yearId;

    Integer auditType;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_final_report_id")
    @OrderBy("orderNo")
    List<AuditStrengthWeaknessPoint> points;

    @Column(name = "approved_emp_id")
    Long approvedEmpId;

    Integer status;

    @ManyToOne
    @JsonIgnoreProperties({"department","color","dob","gender","deleted","jobId",
            "mobileNumber","user","shortName","createdAt","updatedAt","createdBy","updatedBy"})
    @JoinColumn(name = "approved_emp_id", insertable = false,updatable = false,nullable = true)
    Employee approvedEmployee;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date approvedDate;


    @Lob
    String remarks;

    @JsonIgnoreProperties({"createdAt","updatedAt","createdBy","updatedBy"})
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_report_id")
    List<AuditPlan> auditPlans;



    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
