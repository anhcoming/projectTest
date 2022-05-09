package com.viettel.hstd.dto.hstd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RecruitmentProgressEmployeeDTO {

    @Getter
    @Setter
    public static class Request{
        private Long employeeId;
        private String employeeCode;
        private String employeeName;
        private String employeeEmail;
        private Boolean isHr;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response{
        private Long employeeId;
        private String employeeCode;
        private String employeeName;
        private String employeeEmail;
        private Boolean isHr;
    }
}
