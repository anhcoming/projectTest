package com.viettel.hstd.entity.vps;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Table(name = "POSITION")
@Getter
@Setter
@Entity
public class PositionEntity {
    @Id
    @Column(name = "POSITION_ID")
    private Long positionId;
    @Column(name = "POSITION_CODE")
    private String positionCode;
    @Column(name = "POSITION_NAME")
    private String positionName;
//    @Column(name = "NORM")
//    private String norm;
//    @Column(name = "EXPERIENCE")
//    private String experience;
//    @Column(name = "GENDER")
//    private Gender gender = Gender.MALE;
//    @Column(name = "DESCRIPTION")
//    private String description;
//    @Column(name = "IS_ACTIVE")
//    private Boolean isActive;
//    @Column(name = "LABOUR_TYPE_ID")
//    private Long labourTypeId;
//    @Column(name = "POSITION_GROUP_ID")
//    private Long positionGroupId;
//    @Column(name = "EXPIRY_DATE")
//    private LocalDate expiryDate;
//    @Column(name = "EFFECTIVE_DATE")
//    private LocalDate effectiveDate;
//    @Column(name = "POS_ORDER")
//    private Long posOrder;
//    @Column(name = "CREATED_TIME")
//    private LocalDate createdTime;
//    @Column(name = "MODIFIED_TIME")
//    private LocalDate modifiedTime;
//    @Column(name = "MODIFIED_BY")
//    private String modifiedBy;
//    @Column(name = "CREATED_BY")
//    private String createdBy;
//    @Column(name = "LANGUAGE_ID")
//    private Long languageId;
//    @Column(name = "DESCRIPTION2")
//    private String description2;
//    @Column(name = "TYPE")
//    private String type;
//    @Column(name = "IS_PRODUCTIVITY_PARTER")
//    private Boolean isProductivityParter;
//    @Column(name = "POSITION_TYPE")
//    private Long positionType;
//    @Column(name = "MAJOR_STATUS")
//    private Integer majorStatus;
//    @Column(name = "MAJOR_CAREER_MAIN")
//    private Long majorCareerMain;
//    @Column(name = "MAJOR_POSITION_NAME")
//    private String majorPositionName;
//    @Column(name = "MAJOR_ENTERPRISE_TYPE")
//    private Long majorEnterpriseType;
//    @Column(name = "AUTO_GENERATE")
//    private Long autoGenerate;
//    @Column(name = "MAJOR_POSITION_CODE")
//    private String majorPositionCode;
//    @Column(name = "MAJOR_GROUP")
//    private Integer majorGroup;
//    @Column(name = "MAJOR_GROUP_DESCRIPTION")
//    private String majorGroupDescription;
//    @Column(name = "MAJOR_POSITION_GROUP_ID")
//    private Long majorPositionGroupId;
//    @Column(name = "POSITION_SALARY_TYPE")
//    private String positionSalaryType;
//    @Column(name = "CTV_TYPE_ID")
//    private Long ctvTypeId;
//    @Column(name = "MAJOR_ID")
//    private Long majorId;
//    @Column(name = "POSITION_NAME_EN")
//    private String positionNameEn;
}
