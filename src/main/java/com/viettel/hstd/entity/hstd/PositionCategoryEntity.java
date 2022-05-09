package com.viettel.hstd.entity.hstd;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "POSITION_CATEGORY")
public class PositionCategoryEntity {

    @Id
    @GeneratedValue
    @Column(name = "POSITION_CATEGORY_ID")
    private Long id;

    @Column(name = "AREA_CODE")
    private String areaCode;

    @Column(name = "AREA_NAME")
    private String areaName;

    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "BRANCH_NAME")
    private String branchName;

    @Column(name = "JOB_CODE")
    private String jobCode;

    @Column(name = "JOB_NAME")
    private String jobName;

    @Column(name = "POSITION_CODE")
    private String positionCode;

    @Column(name = "POSITION_NAME")
    private String positionName;

    @Column(name = "POSITION_ENG_NAME")
    private String positionEngName;
}
