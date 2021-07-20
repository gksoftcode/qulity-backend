package com.wisecode.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ExceptionLogDTO {
    Long id;
    String employeeName;
    String serviceUrl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date createdDate;
    String exceptionName;
    String message;
}
