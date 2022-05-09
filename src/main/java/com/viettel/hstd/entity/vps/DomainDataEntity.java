package com.viettel.hstd.entity.vps;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Table(name = "DOMAIN_DATA")
@Getter
@Setter
@Entity
public class DomainDataEntity {
    @Id
    @Column(name = "DOMAIN_DATA_ID")
    private Long domainDataId;
    @Column(name = "DATA_ID")
    private Long dataId;
    @Column(name = "DATA_CODE")
    private String dataCode;
    @Column(name = "DATA_NAME")
    private String dataName;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "PATH")
    private String path;
    @Column(name = "FULL_PATH")
    private String fullPath;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "DOMAIN_TYPE_ID")
    private Long domainTypeId;
    @Column(name = "START_DATE")
    private LocalDate startDate;
    @Column(name = "END_DATE")
    private LocalDate endDate;
    @Column(name = "PATH_LEVEL")
    private Integer pathLevel;
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;
}
