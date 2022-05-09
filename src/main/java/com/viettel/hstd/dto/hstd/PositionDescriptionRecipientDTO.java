package com.viettel.hstd.dto.hstd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PositionDescriptionRecipientDTO {
    @Getter
    @Setter
    public static class Request {
        private Long id;
        private Long employeeId;
        private String employeeCode;
        private String employeeName;
        private String employeeEmail;
        private String employeeMobilePhone;
        private Long positionDescriptionId;
        private Boolean delFlag = false;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private Long employeeId;
        private String employeeCode;
        private String employeeName;
        private String employeeEmail;
        private String employeeMobilePhone;
        private Boolean delFlag;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmailResponse{
        private String unitName;
        private String departmentName;
        private String groupName;
        private String positionName;
        private String employeeName;
        private String employeeEmail;
    }
}
