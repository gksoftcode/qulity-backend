package com.wisecode.core.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(of = {"seq"})
public class FileUploadDtls {
    String originalFileName;
    String name;
    String filePath;
    String fileType;
    Integer orderNo;
    Integer version;
    Date issueDate;
    Integer seq;
}
