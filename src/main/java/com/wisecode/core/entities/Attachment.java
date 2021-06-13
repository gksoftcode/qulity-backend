package com.wisecode.core.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wisecode.core.audit.UserDateAudit;
import com.wisecode.core.util.SystemUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "attachment")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"},callSuper = false)
public class Attachment extends UserDateAudit implements BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    String name;
    String mimeType;
    String fileName;

    String mimeTempType;
    String fileTempName;

    String mimePdfType;
    String filePdfName;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date issueDate;
    Integer version;

    @Column(name = "department_id")
    Long departmentId;

    @Column(name = "uuid")
    String uuid;

    @JsonIgnore
    @Lob
    byte[] fileData;

    @JsonIgnore
    @Lob
    byte[] tempFileData;

    @JsonIgnore
    @Lob
    byte[] pdfFileData;

    Integer status;

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
