package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.AcceptJobStatus;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.constant.InterviewResult;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class InterviewSessionCvDTO {
    public static class InterviewSessionCvRequest {
        @NotNull
        public Long interviewSessionId;
        @NotNull
        public Long cvId;
        public Float expertiseGrade;
        public Float culturalAppropriationGrade;
        public Float styleGrade;
        public Float experienceGrade;
        public String characteristicMultipleChoice;
        public String english;
        public Float technologyFamiliarityGrade;
        public String review;
        public InterviewResult result = InterviewResult.NOT_EVALUATE_YET;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate resultEmailSendDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate interviewDate;

        public Float salaryExpectations;
        public Float currentSalary;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate expectedDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate startingDate;
        public String reason;
        public AcceptJobStatus isWork = AcceptJobStatus.NOT_DECIDED_YET;
        public List<FileAttachmentDTO.FileAttachmentRequest> lstAttachment;
        public ContractType contractType;

        public Float sumPoint;
        public Float maxPoint;
        public Boolean isCallVoffice;
        public String signedFile;
        public String transCode;
        public Long interviewSessionCvId;
        public String interviewReportFile;
    }

    public static class InterviewSessionCvResponse {
        public Long interviewSessionCvId;
        public Long interviewSessionId;
        public Long cvId;
        public Float expertiseGrade;
        public Float culturalAppropriationGrade;
        public Float styleGrade;
        public Float experienceGrade;
        public String characteristicMultipleChoice;
        public String english;
        public Float technologyFamiliarityGrade;
        public String review;
        public InterviewResult result = InterviewResult.NOT_EVALUATE_YET;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate resultEmailSendDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate interviewDate;

        public InterviewSessionDTO.InterviewSessionResponse interviewSessionEntity;
        public CvDTO.CvResponse cvEntity;
        public Float salaryExpectations;
        public Float currentSalary;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate expectedDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate startingDate;
        public String reason;
        public AcceptJobStatus isWork = AcceptJobStatus.NOT_DECIDED_YET;
        public List<FileAttachmentDTO.FileAttachmentResponse> lstAttachment;
        public ContractType contractType;

        public Float sumPoint;
        public Float maxPoint;

        public Boolean isCallVoffice;
        public String signedFile;
        public String transCode;
        public String interviewReportFile;
        public Boolean isLock;
    }
}
