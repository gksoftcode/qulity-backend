package com.wisecode.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class TransactionDto {
    String remarks;
    Integer actionType;
    Date createdAt;
    String fullName;
    Long id;
}
