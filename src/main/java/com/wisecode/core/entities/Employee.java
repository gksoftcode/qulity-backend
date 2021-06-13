package com.wisecode.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "employee")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"},callSuper = false)
public class Employee extends UserDateAudit implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_number",unique = true)
    private Long empNumber;

    private String fullName;
    private String shortName;
    private String email;
    private String color;

    @Temporal(TemporalType.DATE)
    private Date dob;
    private Boolean gender =true;
    private Boolean deleted;

    @JsonIgnoreProperties({"active","jobType","jobCategory","createdAt","updatedAt","createdBy","updatedBy"})
    @ManyToOne
    @JoinColumn(insertable = false,updatable = false,name = "job_id")
    private Job job;

    @Column(name = "job_id")
    Long jobId;

    @JsonIgnoreProperties({"createdAt","updatedAt","createdBy","updatedBy","departmentList","manager"})
    @ManyToOne
    @JoinColumn(name = "department_id", insertable = false,updatable = false)
    private Department department;

    @Column(name = "department_id")
    private Long departmentId;

    private String mobileNumber;

    @JsonIgnoreProperties({"password","createdAt","updatedAt","authorities"})
    @OneToOne(mappedBy = "employee",cascade = CascadeType.ALL)
    private User user;


    @Override
    public String toString() {
        return "Employee [id=" + id + ", fullName=" + fullName + "]";
    }

    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
