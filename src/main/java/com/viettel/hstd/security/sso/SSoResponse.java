package com.viettel.hstd.security.sso;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SSoResponse {
    /*from db*/
    private Long id; /* account Id*/
    private Long userInfoId; /* account Id*/
    /*from vps user */
    private String ticket;
    private String email;
    private Long employeeId;
    private String employeeCode;
    private String fullName;
    private String loginName;
    private String encryptedPw;
    private String phoneNumber;
    private String password;
    private Long sysUserId;
    private Boolean isActive;
    /*from sso user*/
    private String userName;
    private String staffCode;

    private Long positionId;
    private String positionName;
    private Long userID;
    private List<GrantedAuth> grantedAuthority = new ArrayList<>();
    private Set<String> permissionSet = new HashSet<>();
    private Set<String> roleSet = new HashSet<>();
    private Set<Long> domainDataSet = new HashSet<>();
    private Long organizationId;
    private Long unitId;
    private String unitName;
    private Long departmentId;
    private String departmentName;

    // Dùng cho acc do hstd tạo
    private Boolean isHsdtAccount = false;
    private Long employeeVhrTempId;
    private Long interviewSessionCvId;
}


