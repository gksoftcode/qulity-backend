package com.wisecode.core.exceptions;

import lombok.ToString;

@ToString(of = {"id"})
public class EmployeeNotFoundException extends Exception {
    private Long id;

    public EmployeeNotFoundException(Long id){
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Employee Not Found ID = "+id;
    }
}
