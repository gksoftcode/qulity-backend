package com.wisecode.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SimpleFileUpload {
    String name;
    Integer orderNo;
    Integer version;
    Long departmentId;
    Date issueDate;
}
