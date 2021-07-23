package com.wisecode.core.mapper;

import com.wisecode.core.dto.AttachmentTransactionDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AttachmentTransactionMapper implements RowMapper<AttachmentTransactionDto> {
    @Override
    public AttachmentTransactionDto mapRow(ResultSet resultSet, int i) throws SQLException {
        AttachmentTransactionDto obj = new AttachmentTransactionDto();
        obj.setActionType(resultSet.getInt("action_type"));
        obj.setCreatedAt(resultSet.getTimestamp("created_at"));
        obj.setId(resultSet.getLong("id"));
        obj.setRemarks(resultSet.getString("remarks"));
        obj.setFullName(resultSet.getString("full_name"));
        return obj;
    }
}
