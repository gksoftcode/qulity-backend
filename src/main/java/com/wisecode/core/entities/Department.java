package com.wisecode.core.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "department")
@Getter
@Setter
@EqualsAndHashCode(of = "id",callSuper = false)
public class Department extends UserDateAudit implements BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer level;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_department_id", insertable = false, updatable = false)
    private Department parentDepartment;

    @Column(name = "parent_department_id")
    private Long parentId;

    @JsonAlias("children")
    @OneToMany(mappedBy = "parentDepartment")
    @OrderBy("orderNo")
    private List<Department> departmentList;
    private Boolean deleted;

    Integer departmentNo;

    @JsonIgnoreProperties({"department","color","dob","gender","deleted","jobId",
           "mobileNumber","user","shortName","createdAt","updatedAt","createdBy","updatedBy"})
    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "manager_id",nullable = true,updatable = false,insertable = false)
    private Employee manager;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name="order_no")
    Integer orderNo = 0;

    @Override
    public String toString() {
        return "Department [id=" + id + ", name=" + name + ", level=" + level + "]";
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
