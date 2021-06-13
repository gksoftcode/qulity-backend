package com.wisecode.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wisecode.core.util.SystemUtil;
import lombok.Data;

import java.util.Date;

@Data
public class AttachmentDto {

    Long id;
    String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date issueDate;
    Integer version;
    Long departmentId;
    String uuid;
    Integer status;
    Integer orderNo;
    String departmentName;
    Integer departmentNo;
    Long tempFileSize= 0L ;
    Long pdfFileSize = 0L;

    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
