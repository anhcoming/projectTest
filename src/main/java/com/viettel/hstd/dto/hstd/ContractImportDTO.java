package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.ContractDuration;
import com.viettel.hstd.constant.ContractImportCategory;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import com.viettel.hstd.entity.hstd.ContractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContractImportDTO {
    public static class ContractImportRequest {
        @NotNull(message = "Loại hợp đồng là bắt buộc")
        public ContractImportCategory category;
        public Integer quarter;
        public Integer year;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate startDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate endDate;
        @NotNull(message = "Không tìm thấy tên file ")
        public String filePath;
    }

    public static class ContractImportResponse {
        public Boolean result;
        public List<ContractImportRead> reads = new ArrayList<>();
    }


    public static class ContractImportRead {
        public String employeeCode;
        public ContractImportError error;
        public ContractImportModel success;

        public ContractImportRead() {
            this.error = new ContractImportError();
            this.success = new ContractImportModel();
        }

    }

    public static class ContractImportError {
        public String contractType;
        public String contractNumber;
        public String effectiveDate;
        public String employeeCode;
        public String employeeName;
        public String nationality;
        public String birthDate;
        public String placeOfBirth;
        public String gender;
        public String trainingLevel;
        public String trainingSpeciality;
        public String personalIdNumber;
        public String personalIdIssuedDate;
        public String personalIdIssuedPlace;
        public String permanentAddress;
        public String mobileNumber;
        public String positionName;
        public String unitName;
        public String departmentName;
        public String contractDuration;

        public String bank;
        public String negotiateSalary;
        public String percentSalary;
        public String accountNumber;
        public String laborNoteNumber;
        public String laborNoteDate;
        public String laborNoteAddress;

        public String basicSalary;
        public String payRate;
        public String payGrade;
        public String payRange;

        public String newContractDuration;
        public String newContractNumber;

        public String newBasicSalary;
        public String newPayRate;
        public String newPayGrade;
        public String newPayRange;

        public String reason;
        public Integer line;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ContractImportModel extends ContractEntity {
        private String unitName;
        private String departmentName;
        private Integer negotiateSalary;
        private Float percentSalary = 100f;

        private String laborNoteNumber;
        private LocalDate laborNoteDate;
        private String laborNoteAddress;

        private ContractDuration newContractDuration;
        private String newContractNumber;

        private String newBasicSalary;
        private String newPayRate;
        private String newPayGrade;
        private String newPayRange;
    }
}
