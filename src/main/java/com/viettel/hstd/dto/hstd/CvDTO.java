package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.constant.Gender;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
//import com.viettel.hstd.entity.hstd.CandidateEntity;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CvDTO {
    public static class CvRequest {
        public Long cvId;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate applyDate;
        public String applyPosition;
        public String positionCode;
        public Long positionId;
        public Boolean interviewState = false;
        public Integer yearsExperience;
        public String fileName;
        public String filePath;
        public Long candidateId;
        public String fullName;
        public Gender gender = Gender.MALE;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate userBirthday;
        @NotBlank(message = "Email không được để trống")
        public String email;
        @NotBlank(message = "Số điện thoại không được để trống")
        public String phoneNumber;
        public String address;
        public String technicalExpertiseProfession;
        public String school;
        public String major;
        public String personalIdNumber;
        public String permanenceAddress;
        public String summaryWorkingExperience;
        public ContractType contractType;
        public boolean isActive = true;

        public String diplomaNo;
        public String registerNo;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate issuedOn;
        public String rector;
        public Long unitId  = 9004488L;
        public String unitName = "KCQ TCT Công trình";
    }

    public static class CVExcelRequest extends CvRequest {
        public int numberOrder;
        public String fullName;
        public Gender gender = Gender.MALE;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate userBirthday;
        public String email;
        public String phoneNumber;
        public String address;
        public String positionCode;
        public String applyPosition;
        public String technicalExpertiseProfession;
        public String school;
        public String major;
        public String personalIdNumber;
        public int yearsExperience;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate applyDate;
        public String isActive;
        public boolean interviewState = false;

        public String diplomaNo;
        public String registerNo;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate issuedOn;
        public String rector;
        public Long unitId  = 9004488L;
        public String unitName = "KCQ TCT Công trình";
    }

    public static class CvResponse {
        public Long cvId;
        public String fullName;
        public Gender gender = Gender.MALE;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate userBirthday;
        public String email;
        public String phoneNumber;
        public String address;
        public String technicalExpertiseProfession;
        public String school;
        public String major;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate applyDate;
        public String applyPosition;
        public String positionCode;
        public Long positionId;
        public Boolean interviewState;
        public Integer yearsExperience;
        public String fileName;
        public String filePath;
        public Long candidateId;
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        public LocalDateTime latestInterviewDate;
        public Long numberOfInterview;
        public String personalIdNumber;
        public String permanenceAddress;
        public String summaryWorkingExperience;
        public Boolean isActive;
        public boolean isLock = false;

        public String diplomaNo;
        public String registerNo;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate issuedOn;
        public String rector;
        public Long unitId  = 9004488L;
        public String unitName = "KCQ TCT Công trình";
    }

    public static class CVExcelResponse extends CvResponse{
        public int numberOrder;
        public String reason;
    }
}
