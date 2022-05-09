package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "SYS_LOG")
@SequenceGenerator(name = "SYS_LOG_GEN", initialValue = 1, allocationSize = 1, sequenceName = "SYS_LOG_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class SysLogEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SYS_LOG_GEN")
    @Column(name = "SYS_LOG_ID")
    private long sysLogId;
    @Column(name = "SYS_USER_ID")
    private Long sysUserId;
    @Column(name = "WORKSTATION")
    private String workStation;
    @Column(name = "CONTENT")
    private String content;
    @Column(name = "TABLE_NAME")
    private String tableName;

    @Column(name = "LOGIN_NAME")
    private String loginName;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "DATA")
    private String data;
}
