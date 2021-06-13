package com.wisecode.core.entities;

import com.wisecode.core.RoleName;
import com.wisecode.core.util.SystemUtil;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role implements BaseEntity, GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 60)
    private RoleName name;

    @Column(name = "arabic_name")
    String arabicName;

    public Role() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) || name == role.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public Role(RoleName name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public String getArabicName() {
        return arabicName;
    }

    public void setArabicName(String arabicName) {
        this.arabicName = arabicName;
    }
    @Transient
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }

    @Override
    public String getAuthority() {
        return getName().name();
    }
}
