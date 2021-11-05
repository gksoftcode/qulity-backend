package com.wisecode.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "work_guide")
@Getter
@Setter
@EqualsAndHashCode(of = "id",callSuper = false)
@SqlResultSetMappings({@SqlResultSetMapping(name = "dtoSearch",
        classes = {@ConstructorResult(targetClass = com.wisecode.core.dto.WorkGuideDto.class,
                columns = {
                        @ColumnResult(name = "id",type = Long.class),
                        @ColumnResult(name = "status" ,type = Integer.class),
                        @ColumnResult(name = "type", type = Integer.class),
                        @ColumnResult(name = "departmentId",type = Long.class),
                        @ColumnResult(name = "version",type = Integer.class),
                        @ColumnResult(name = "departmentName",type = String.class),
                        @ColumnResult(name = "departmentNo",type = Integer.class),
                        @ColumnResult(name = "level",type = Integer.class),
                        @ColumnResult(name = "lastActionDate",type = Instant.class)
        })})})
@NamedNativeQuery(name = "search123",query = "select tt.* from (select wg.id as id,wg.status as status,wg.work_guide_type as type ,wg.department_id as departmentId,wg.version as version" +
        " ,dep.name as departmentName , dep.department_no as departmentNo , dep.level level ," +
        " (select max(trans.created_at) from work_guide_transaction trans where trans.work_guide_id = wg.id) as lastActionDate " +
        " from work_guide wg inner join department dep on wg.department_id = dep.id where (:status_id = -1 or wg.status = :status_id) " +
        "and (:type_id = -1 or wg.work_guide_type = :type_id)  " +
        "and (:department_list is null or dep.id in (:department_list)) ) tt ",resultSetMapping = "dtoSearch")
public class WorkGuide extends UserDateAudit implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4000)
    String objective;

    @Temporal(TemporalType.DATE)
    Date issueDate;

    @Column(length = 255)
    String documentNo;

    @Column(length = 4000)
    String domain;

    @Column(name = "department_id")
    Long departmentId;

    @JsonIgnoreProperties({"departmentList","createdBy","updatedBy","createdAt","updatedAt"})
    @ManyToOne
    @JoinColumn(name = "department_id" ,insertable = false,updatable = false)
    Department department;
    Integer version;

    Integer status;

    @Column(name = "work_guide_type")
    Integer type;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "work_guide_id")
    @OrderBy("orderNo")
    List<WorkGuideReference> references;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "work_guide_id")
    @OrderBy("orderNo")
    List<WorkGuideDefinition> definitions;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "work_guide_id")
    @OrderBy("orderNo")
    List<WorkGuideResponsibility> responsibilities;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "work_guide_id")
    @OrderBy("orderNo")
    List<WorkGuideProcedure> procedures;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "work_guide_id")
    @OrderBy("orderNo")
    List<WorkGuidePointer> pointers;

    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
