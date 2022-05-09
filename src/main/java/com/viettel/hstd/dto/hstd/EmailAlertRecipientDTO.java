package com.viettel.hstd.dto.hstd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EmailAlertRecipientDTO {

    @Getter
    @Setter
    public static class Request{
        private Long employeeId;
        private String employeeCode;
        private String employeeName;
        private String employeeEmail;
        private Long emailAlertId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response{
        private Long employeeId;
        private String employeeCode;
        private String employeeName;
        private String email;
    }
}
