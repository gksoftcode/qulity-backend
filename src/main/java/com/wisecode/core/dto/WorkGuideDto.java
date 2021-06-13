package com.wisecode.core.dto;

import com.wisecode.core.util.SystemUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Data
public class WorkGuideDto implements Serializable {
    Long id;
    Long departmentId;
    Integer status;
    Integer type;
    String departmentName;
    Instant lastActionDate;
    Integer version;
    Integer departmentNo;
    Integer level;
    public WorkGuideDto(){}
    public WorkGuideDto(Long id, Integer status, Integer type, Long departmentId,
                        Integer version, String departmentName,Integer departmentNo,Integer level) {
        this.id = id;
        this.departmentId = departmentId;
        this.status = status;
        this.type = type;
        this.version = version;
        this.departmentName = departmentName;
        this.departmentNo = departmentNo;
        this.level = level;
    }

    public WorkGuideDto(Long id, Integer status, Integer type, Long departmentId,
                        Integer version, String departmentName,Integer departmentNo,Integer level, Instant lastActionDate) {
        this.id = id;
        this.departmentId = departmentId;
        this.status = status;
        this.type = type;
        this.departmentName = departmentName;
        this.departmentNo = departmentNo;
        this.level = level;
        this.lastActionDate = lastActionDate;
        this.version = version;
    }

    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }

    public String getEncDepartmentId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getDepartmentId().toString());
        }
        return null;
    }
}
