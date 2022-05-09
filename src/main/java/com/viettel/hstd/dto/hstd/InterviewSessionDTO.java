package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeDeserializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeSerializer;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import com.viettel.hstd.dto.vps.EmployeeVhrDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InterviewSessionDTO {
    @Getter
    @Setter
    public static class InterviewSessionRequest {
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        public LocalDateTime startDate;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        public LocalDateTime endDate;
        public String name;
        public List<EmployeeInterviewSessionDTO.EmployeeInterviewSessionRequest> lstEmployee;

        public String interviewLocation;
        @NotNull
        public Long positionId;
        public String positionCode;
        public String positionName;
        @NotNull
        public Long leaderId;
        public String leaderName;
        public String leaderEmail;

        public String description;
        public Long unitId;
        public Long departmentId;

    }

    public static class InterviewExistedRequest {
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        public LocalDateTime startDate;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        public LocalDateTime endDate;
        public Long positionId;
    }

    public static class InterviewSessionResponse {
        public Long interviewSessionId;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        public LocalDateTime startDate;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        public LocalDateTime endDate;
        public List<EmployeeVhrDTO.EmployeeVhrResponse> lstEmployee;
        public Integer totalCandidate;
        public String name;
        public String interviewLocation;

        public Long positionId;
        public String positionCode;
        public String positionName;
        public String leaderId;
        public String leaderName;
        public String leaderEmail;

        public String description;

        public Long unitId;
        public String unitName;
        public Long departmentId;
        public String departmentName;
        public Boolean isLock;

    }
}
