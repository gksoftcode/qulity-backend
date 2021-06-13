package com.wisecode.core.dto;

import java.util.Date;

public interface WorkGuideDtoInterface {
    public Long getId() ;

    public Long getDepartmentId() ;

    public Integer getStatus() ;

    public Integer getType() ;

    public String getDepartmentName();

    public Integer getVersion();

    public Integer getLevel();

    public Integer getDepartmentNo();

    public Date getLastActionDate();

    public String getEncId();
    public String getEncDepartmentId();
}
