package com.viettel.hstd.dto.vps;

import java.util.HashSet;
import java.util.Set;

public class SysUserDTO {
    public static class SysUserResponse {
        public Long sysUserId;
        public String fullName;
        public String loginName;
        public String employeeCode;
        public String phoneNumber;
        public String email;
    }

    public static class UserResponse{
        public String email;
        public String employeeCode;
        public String fullName;
        public String loginName;
        public String phoneNumber;
        public Long sysUserId;
        public Boolean isActive;
        public String staffCode;
        public Long unitId;
        public String unitName;
        public Long departmentId;
        public String departmentName;
        public Long positionId;
        public String positionName;
        public Set<String> permissions = new HashSet<>();
        public Set<String> permissionPages = new HashSet<>();
        public Set<String> roles = new HashSet<>();
    }
}



