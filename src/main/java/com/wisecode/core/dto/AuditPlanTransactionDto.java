package com.wisecode.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AuditPlanTransactionDto {
    Date createdAt;
    Integer actionType;
    String fullName;
    Long id;
    String remarks;
}
