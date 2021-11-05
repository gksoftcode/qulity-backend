package com.wisecode.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "procedure_step")
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

    @JsonIgnoreProperties({"fileData","tempFileData","pdfFileData","createdAt","updatedAt","createdBy","updatedBy"})
    @ManyToMany
    List<Attachment> files;

    @ManyToMany
    List<DocumentType> documentTypes;

    @ElementCollection
    List<String> docs;
    
    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
