package com.wisecode.core.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.controller.AuditPlanController;
import com.wisecode.core.util.SystemUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "corrective_action")
@Getter
@Setter
@EqualsAndHashCode(of = "id",callSuper = false)
public class CorrectiveAction extends UserDateAudit implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }

    @Lob
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date descDate;

    @Column(name = "desc_emp_id")
    Long descEmpId;
    @ManyToOne
    @JsonIgnoreProperties({"department","color","dob","gender","deleted","jobId",
            "mobileNumber","user","shortName","createdAt","updatedAt","createdBy","updatedBy"})
    @JoinColumn(name = "desc_emp_id", insertable = false,updatable = false)
    Employee descEmployee;

    @Lob
    String immediateCorrection;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date immediateDate;
    @Column(name = "immediate_emp_id")
    Long immediateEmpId;
    @ManyToOne
    @JsonIgnoreProperties({"department","color","dob","gender","deleted","jobId",
            "mobileNumber","user","shortName","createdAt","updatedAt","createdBy","updatedBy"})
    @JoinColumn(name = "immediate_emp_id", insertable = false,updatable = false)
    Employee immediateEmployee;

    @Lob
    String rootReason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date rootReasonDate;
    @Column(name = "root_reason_emp_id")
    Long rootReasonEmpId;
    @ManyToOne
    @JsonIgnoreProperties({"department","color","dob","gender","deleted","jobId",
            "mobileNumber","user","shortName","createdAt","updatedAt","createdBy","updatedBy"})
    @JoinColumn(name = "root_reason_emp_id", insertable = false,updatable = false)
    Employee rootReasonEmployee;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "corrective_action_id")
    @OrderBy("orderNo")
    List<CorrectiveActionStep> correctionSteps;

    @Column(name = "audit_item_id")
    Long auditItemId;

    @Column(name = "execute_emp_id")
    Long executeEmpId;
    @ManyToOne
    @JsonIgnoreProperties({"department","color","dob","gender","deleted","jobId",
            "mobileNumber","user","shortName","createdAt","updatedAt","createdBy","updatedBy"})
    @JoinColumn(name = "execute_emp_id", insertable = false,updatable = false)
    Employee executeEmployee;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date executeDate;

    @Column(name = "follow_emp_id")
    Long followEmpId;
    @ManyToOne
    @JsonIgnoreProperties({"department","color","dob","gender","deleted","jobId",
            "mobileNumber","user","shortName","createdAt","updatedAt","createdBy","updatedBy"})
    @JoinColumn(name = "follow_emp_id", insertable = false,updatable = false)
    Employee followEmployee;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date followDate;
    @Lob
    String followDescription;
    @Column(name = "approved_emp_id")
    Long approvedEmpId;
    @ManyToOne
    @JsonIgnoreProperties({"department","color","dob","gender","deleted","jobId",
            "mobileNumber","user","shortName","createdAt","updatedAt","createdBy","updatedBy"})
    @JoinColumn(name = "approved_emp_id", insertable = false,updatable = false)
    Employee approvedEmployee;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date approvedDate;

    Integer status = AuditPlanController.CORRECTIVE_ACTION_STATUS_NEW;
    Integer approved = AuditPlanController.CORRECTIVE_ACTION_APPROVED_NEW;
}
