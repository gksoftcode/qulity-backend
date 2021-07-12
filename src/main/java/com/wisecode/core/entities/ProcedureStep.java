package com.wisecode.core.entities;

import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.model.FileUploadDtls;
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
public class ProcedureStep extends UserDateAudit implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    String description;

    @Column(length = 1000)
    String remarks;
    @Column(name = "order_no")
    Integer orderNo;
    Boolean val1;
    Boolean val2;
    Boolean val3;
    Boolean val4;
    Boolean val5;
    Boolean val6;
    Boolean val7;
    Boolean val8;
    Boolean val9;

    @ManyToMany
    List<Attachment> files;
    
    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
