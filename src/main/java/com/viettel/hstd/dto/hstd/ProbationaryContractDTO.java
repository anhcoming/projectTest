package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
        import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.Gender;
import com.viettel.hstd.constant.NewContractStatus;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
        import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;

import javax.persistence.Column;
import java.time.LocalDate;

public class ProbationaryContractDTO {
    public static class ProbationaryContractRequest extends ContractDTO.ContractRequest {
//        public Long contractId;
//        public int contractType = 5;
//        public String contractNumber;
//        public String signedPlace;
//        @JsonSerialize(using = CustomLocalDateSerializer.class)
//        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
//        public LocalDate effectiveDate;
//        @JsonSerialize(using = CustomLocalDateSerializer.class)
//        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
//        public LocalDate expiredDate;
//        @JsonSerialize(using = CustomLocalDateSerializer.class)
//        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
//        public LocalDate signedDate;
//        public Integer accountId;
//        public Integer accountCode;
//        public Integer accountName;
//        public Integer employeeId;//
//        public String employeeCode;//
//        public String employeeName;//
//        public String nationality;//
//        @JsonSerialize(using = CustomLocalDateSerializer.class)
//        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
//        public LocalDate birthDate;//
//        public String placeOfBirth;//
//        public Gender gender = Gender.MALE;//
//        public String trainingLevel;//
//        public String trainingSpeciality;//
//        public String personalIdNumber;//
//        @JsonSerialize(using = CustomLocalDateSerializer.class)
//        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
//        public LocalDate personalIdIssuedDate;//
//        public String personalIdIssuedPlace;//
//        public String permanentAddress;//
//        public String mobileNumber;//
//        public Integer positionId;
//        public String positionCode;
//        public String positionName;
//        public String accountNumber;
//        public String bank;
//        public String currentAddress;
//        public int contractDuration;

        // Custom fields for ProbationaryContract
        public String laborNoteNumber;///
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate laborNoteDate;
        public String laborNoteAddress;
        public Integer negotiateSalary;
        public int probationaryContractType = 1;
        public Float percentSalary = 100f;
    }

    public static class ProbationaryContractResponse extends ContractDTO.ContractResponse {

        // Custom fields for ProbationaryContract
        public String laborNoteNumber;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate laborNoteDate;
        public String laborNoteAddress;
        public Long negotiateSalary;
        public Float percentSalary = 100f;
        public int probationaryContractType = 1;
    }

    public static class ProbationaryNewStatusRequest extends ContractDTO.ContractNewStatusRequest {
    }

    public static class ProbationaryExportContractRequest {
        public Long contractId;
    }
}