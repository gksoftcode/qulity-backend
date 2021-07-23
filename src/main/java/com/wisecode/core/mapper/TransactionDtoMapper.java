package com.wisecode.core.mapper;

import com.wisecode.core.dto.TransactionDto;
import org.springframework.jdbc.core.RowMapper;

public abstract class TransactionDtoMapper <T extends TransactionDto> implements RowMapper<T> {

}
