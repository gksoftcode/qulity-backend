package com.wisecode.core.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeRole {
   Employee employee;
   Role role;
}
