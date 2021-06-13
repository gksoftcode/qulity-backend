package com.wisecode.core.entities;

import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"},callSuper = false)
public class Job extends UserDateAudit implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean active;
    private Integer jobType = 2;
    private Integer jobCategory;

    @Override
    public String toString() {
        return "Job [id=" + id + ", name=" + name + "]";
    }

    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
         return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
