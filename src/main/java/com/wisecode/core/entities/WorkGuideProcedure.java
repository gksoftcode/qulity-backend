package com.wisecode.core.entities;

import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@EqualsAndHashCode(of = "id",callSuper = false)
public class WorkGuideProcedure extends UserDateAudit implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4000)
    String description;
    @Column(length = 4000)
    String remarks;
    @Column(name = "order_no")
    Integer orderNo;

    @Column
    String period;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "procedure_id")
    List<ProcedureStep> steps;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "procedure_id")
    List<WorkGuideProcedureResponsibility> responsibilities;

    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
