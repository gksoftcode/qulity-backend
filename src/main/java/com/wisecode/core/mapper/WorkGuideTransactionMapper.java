package com.wisecode.core.mapper;

import com.wisecode.core.dto.WorkGuideTransactionDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkGuideTransactionMapper implements RowMapper<WorkGuideTransactionDto> {
    @Override
    public WorkGuideTransactionDto mapRow(ResultSet resultSet, int i) throws SQLException {
        WorkGuideTransactionDto obj = new WorkGuideTransactionDto();
        obj.setActionType(resultSet.getInt("action_type"));
        obj.setCreatedAt(resultSet.getTimestamp("created_at"));
        obj.setId(resultSet.getLong("id"));
        obj.setRemarks(resultSet.getString("remarks"));
        obj.setFullName(resultSet.getString("full_name"));
        return obj;
    }
}
