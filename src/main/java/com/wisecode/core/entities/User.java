package com.wisecode.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wisecode.core.audit.DateAudit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user_tbl",uniqueConstraints =
        {@UniqueConstraint(columnNames = {"user_name"})
        })
@Getter
@Setter
@EqualsAndHashCode(of = {"id"},callSuper = false)
public class User extends DateAudit implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name="user_name")
    String username;

    @Column(name="user_pwd")
    String password;

    @Column(name="is_active")
    Boolean active;

    @Column(name = "theme_name")
    String themeName;

    @Column(name = "menu_mode")
    String menuMode;

    @Column(name = "layout")
    String layout;

    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_id_ref")
    Employee employee;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getActive();
    }
}
