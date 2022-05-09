package com.viettel.hstd.dto.hstd;

import javax.validation.constraints.NotNull;

public class EmployeeInterviewSessionDTO {
    public static class EmployeeInterviewSessionRequest {

        public Long employeeInterviewSessionId;
        @NotNull
        public Long employeeId;
        public String fullname;
        @NotNull
        public Long interviewSessionId;


    }

    public static class EmployeeInterviewSessionResponse {
        public Long employeeInterviewSessionId;
        public Long employeeId;
        public Long interviewSessionId;
    }
}
