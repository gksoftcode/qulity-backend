package com.wisecode.core.entities;

import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@EqualsAndHashCode(of = "id",callSuper = false)
public class WorkGuideDefinition extends UserDateAudit implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    String name;

    @Column(length = 2000)
    String description;

    @Column(name = "order_no")
    Integer orderNo;

    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
