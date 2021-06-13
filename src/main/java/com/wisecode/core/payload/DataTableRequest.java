package com.wisecode.core.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DataTableRequest<T> {
    @NotNull
    @Digits(fraction = 0,integer = 10000)
    Integer currentPage;

    @NotNull
    @Digits(fraction = 0,integer = 10000)
    Integer pageSize;

    String sortBy = "";
    Boolean sortDesc = false;

    T data;
}
