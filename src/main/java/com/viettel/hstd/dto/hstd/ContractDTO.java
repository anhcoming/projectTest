package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContractDTO {
    public static class ContractRequest {
        public Long contractId;
        public int contractType = 1;
        public String contractNumber;
        public String signedPlace;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate effectiveDate;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate expiredDate;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate signedDate;
        public Long accountId;
        public String accountCode;
        public String accountName;
        public Long employeeId;
        public String employeeCode;
        public String employeeName;
        public String nationality;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate birthDate;
        public String placeOfBirth;
        public Gender gender = Gender.MALE;
        public String trainingLevel;
        public String trainingSpeciality;
        public String personalIdNumber;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate personalIdIssuedDate;
        public String personalIdIssuedPlace;
        public String permanentAddress;
        public String mobileNumber;
        //
//    public String email;
        public Long positionId;
        public String positionCode;
        public String positionName;
        public String accountNumber;
        public String bank;
        public String currentAddress;
        public String contractFile;
        public String contractFileEncodePath;
        public Boolean isCallVoffice;
        public String signedFile;
        public String signFileEncodePath;
        public String transCode;
        public ContractDuration contractDuration = ContractDuration.ONE_YEAR;
        /**
         * Hop dong da duoc cham dut hay chua?
         */
        public Boolean isTerminate = false;
        public Long unitId = 9004488l; // KCQ
        public Long departmentId = 9004497l; // Tổ chức lao động

        /**
         * Tình trạng khi tạo mới hợp đồng
         */
        public NewContractStatus newContractStatus = NewContractStatus.SENT_TO_VOFFICE;
        public boolean isCreatedByHsdtService = false;

        public String employeeSignedFile;
        public String employeeSignedFileEncodePath;
        public String signatureAbsolutePath;
        public String signatureFileEncodePath;
        public String signatureName;

        public String contractCode;


        public ResignStatus resignStatus = ResignStatus.NOT_IN_RESIGN_SESSION;
        public Boolean isActive = true;
    }

    public static class ContractResponse {
        public Long contractId;
        public ContractType contractType = ContractType.UNKNOWN;
        public String contractNumber;
        public String signedPlace;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate effectiveDate;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate expiredDate;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate signedDate;
        public Integer accountId;
        public Integer accountCode;
        public Integer accountName;
        public Integer employeeId;
        public String employeeCode;
        public String employeeName;
        public String nationality;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate birthDate;
        public String placeOfBirth;
        public Gender gender = Gender.MALE;
        public String trainingLevel;
        public String trainingSpeciality;
        public String personalIdNumber;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate personalIdIssuedDate;
        public String personalIdIssuedPlace;
        public String permanentAddress;
        public String mobileNumber;
        public Integer positionId;
        public String positionCode;
        public String positionName;
        public String accountNumber;
        public String bank;
        public Boolean isCallVoffice;
        public String signedFile;
        public String signedFileEncodePath;
        public String transCode;
        public String currentAddress;
        public String contractFile;
        public String contractFileEncodePath;
        public ContractDuration contractDuration = ContractDuration.ONE_YEAR;
        public String contractDurationVietnamese;
        public Long unitId;
        public String unitName;
        public Long departmentId;
        public String departmentName;
        public ResignStatus resignStatus = ResignStatus.NOT_IN_RESIGN_SESSION;


        public String timeLeft;
        public NewContractStatus newContractStatus = NewContractStatus.HAVENT_SENT_REQUEST_TO_VHR;
        public boolean isCreatedByHsdtService = false;

        public String signatureName;
        public String signaturePath;
        public String signatureFileEncodePath;

        public String contractCode;

        public String employeeSignedFile;
        public String employeeSignedFileEncodePath;
        public boolean isActive = true;
        public Boolean isTerminate = false;
        public String timeDiffStartDateAndSignDate;
    }

    public static class UpdateResignStatusRequest {
        public List<Long> contractIdList = new ArrayList<>();
        public ResignStatus resignStatus = ResignStatus.NOT_IN_RESIGN_SESSION;
    }

    public static class UpdateTransCodeRequest {
        public Long contractId;
        public String transCode;
    }

    public static class CreatePDFContractAndSignedByUserRequest {
        public Long contractId;
        public String signMixedFileName;
    }

    public static class ContractAddEmployeeSignature {
        public Long contractId;
        public String signatureFileName;
        public String signatureFileEncodePath;
    }

    public static class ContractNewStatusRequest {
        public Long contractId;
        public NewContractStatus newContractStatus = NewContractStatus.HAVENT_SENT_REQUEST_TO_VHR;
    }

    public static class ContractChangeSignedFileRequest {
        public Long contractId;
        public String signedFile;
        public String contractFile;
    }

    public static class ContractExportRequest {
        public Long contractId;
    }

    public static class DanhSachDeXuatNhanSuResponse {
        public int index;
        public String fullName;
        public int birthYear;
        public Gender gender;
        public String trainingLevel;
        public String trainingSpeciality;
        public String trainingPlace;
        public Float interviewScore;
        public Float styleScore;
        public String englishScore;
        public Float technologyFamiliarityGrade;
        public InterviewResult interviewResult;
        public String positionName;
        public ContractType contractType;
        public ContractDuration contractDuration;
        public String compensation;
    }
}
