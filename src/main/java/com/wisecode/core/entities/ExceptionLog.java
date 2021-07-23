package com.wisecode.core.entities;

import com.wisecode.core.util.SystemUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "EXCEPTION_LOG")
@Setter
@Getter
@ToString
@EqualsAndHashCode(of = {"id"})
public class ExceptionLog implements Serializable, BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "EXCEPTION_NAME")
    private String exceptionName;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "USER_NUMBER")
    private Long userNumber;

    @Column(name = "SERVICE_URL")
    private String serviceUrl;


    @Column(name ="response_status")
    private String responseStatus;

    @Column(name = "CREATE_DATE")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name ="user_name")
    private String userName;

    @Lob
    private String logTrace;
    @Override
    public String getEncId() {
        if(getId()!= null){
            return SystemUtil.encrypt(getId().toString());
        }
        return null;
    }
}
