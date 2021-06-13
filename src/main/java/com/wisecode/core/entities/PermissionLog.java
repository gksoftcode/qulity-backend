package com.wisecode.core.entities;

import com.wisecode.core.audit.UserDateAudit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class PermissionLog extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;

    Integer actionType;
}
