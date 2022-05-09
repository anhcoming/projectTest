package com.viettel.hstd.entity.vps;

import lombok.Getter;
import lombok.Setter;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Time;

@Table(name = "SYS_USER")
@Getter
@Setter
@Entity
public class SysUserEntity {
    @Id
    @Column(name = "SYS_USER_ID")
    private Long sysUserId;
    @Column(name = "LOGIN_NAME")
    private String loginName;
    @Column(name = "FULL_NAME")
    private String fullName;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "NEW_ID")
    private Integer newId;
    @Column(name = "CHANGE_PASSWORD_DATE")
    private Time changePasswordDate;
    @Column(name = "NEED_CHANGE_PASSWORD")
    private String needChangePassword;
    @Column(name = "SYS_GROUP_ID")
    private Long sysGroupId;
    @Column(name = "SALE_CHANNEL")
    private String saleChannel;
    @Column(name = "PARENT_USER_ID")
    private Long parentUserId;
    @Column(name = "TYPE_USER")
    private Boolean typeUser;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "TAX_CODE")
    private String taxCode;
    @Column(name = "CONTRACT_CODE")
    private String contractCode;
    @Column(name = "TAX_CODE_USER")
    private String taxCodeUser;
    @Column(name = "ACCOUNT_NUMBER")
    private String accountNumber;
    @Column(name = "BANK")
    private String bank;
    @Column(name = "BANK_BRANCH")
    private String bankBranch;
    @Column(name = "CREATED_DATE")
    private Time createdDate;
    @Column(name = "COMPANY_PARTNER")
    private String companyPartner;
    @Column(name = "TEMP_LOCK_COUNT")
    private Long tempLockCount;
    @Column(name = "DATE_LOCK_COUNT")
    private Time dateLockCount;
    @Column(name = "OCCUPATION")
    private String occupation;
    @Column(name = "CMT")
    private String cmt;
    @Column(name = "PARENT_USER_ID_HOLIDAY")
    private Long parentUserIdHoliday;
    @Column(name = "TOKEN")
    private String token;
    @Column(name = "USER_BIRTHDAY")
    private Time userBirthday;
    @Column(name = "IMPACT_USER")
    private Long impactUser;
    @Column(name = "IS_CONFIRM")
    private Boolean isConfirm;
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;
    @Column(name = "APPROVED_USER")
    private Integer approvedUser;
    @Column(name = "APPROVED_DATE")
    private Time approvedDate;
    @Column(name = "ACTIVE_USER")
    private Long activeUser;
    @Column(name = "ACTIVE_DATE")
    private Time activeDate;
    @Column(name = "APPROVED_USER_TCT")
    private Long approvedUserTct;
    @Column(name = "APPROVED_DATE_TCT")
    private Time approvedDateTct;
    @Column(name = "REASON_REJECT")
    private String reasonReject;
    @Column(name = "PROVINCE_ID_XDDD")
    private Long provinceIdXddd;
    @Column(name = "PROVINCE_NAME_XDDD")
    private String provinceNameXddd;
    @Column(name = "POSITION_ID")
    private Long positionId;
    @Column(name = "POSITION_NAME")
    private String positionName;
    @Column(name = "TEMP_PARENT_ID")
    private Integer tempParentId;
    @Column(name = "PROPOSER")
    private Integer proposer;
    @Column(name = "PROPOSE_DATE")
    private Time proposeDate;
}

