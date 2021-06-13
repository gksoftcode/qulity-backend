package com.wisecode.core.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataTableResponse {
    Long total;
    int currentPage;
    int pageSize;
    List<?> data;
}
