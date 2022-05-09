package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.viettel.hstd.constant.Gender;
import com.viettel.hstd.constant.KpiGrade;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeMonthlyReviewDTO {
    public static class EmployeeMonthlyReviewRequest {
        public Long employeeId;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate month;
        public KpiGrade grade = KpiGrade.UNKNOWN;
    }

    public static class EmployeeMonthlyReviewResponse {
        public Long employeeMonthlyReviewId;
        public Long employeeId;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate month;
        public KpiGrade grade = KpiGrade.UNKNOWN;
    }

    public static class EmployeeYearlyReviewResponse {
        public Long employeeId;
        public String employeeCode;
        public String fullName;
        public int birthYear;
        public Gender gender = Gender.MALE;
        public Long positionId;
        public String positionName;
        public String unitName;
        public String departmentName;
        public String trainingLevel;
        public String trainingSpeciality;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate contractEffectiveDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate contractExpiredDate;
        @JsonDeserialize(contentUsing = CustomLocalDateDeserializer.class)
        @JsonSerialize(contentUsing = CustomLocalDateSerializer.class)
        public List<LocalDate> listMonth = new ArrayList<>();
        public List<String> listGrade = new ArrayList<>();
        public List<EmployeeMonthlyReviewResponse> listMonthlyReview = new ArrayList<>();
        public Float averageScore;
        public String note;
    }

    public static class EmployeeYearlyReviewResponseList {
        public List<EmployeeYearlyReviewResponse> employeeYearlyReviewResponseList = new ArrayList<>();
    }

}
