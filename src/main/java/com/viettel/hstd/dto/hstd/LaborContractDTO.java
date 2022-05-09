package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.ContractDuration;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.constant.Gender;
import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;

import java.time.LocalDate;

public class LaborContractDTO {
    public static class LaborContractRequest extends ContractDTO.ContractRequest {

        // Custom field for Labor Contract
        public String basicSalary;//
        public String payRate;// hệ số lương
        public String payGrade;// ngạch lương
        public String payRange;// bậc lương

    }

    public static class LaborContractResponse extends ContractDTO.ContractResponse {
        public ContractType contractType = ContractType.LABOR_CONTRACT;

        // Custom field for Labor Contract
        public String basicSalary;
        public String payRate;
        public String payGrade;
        public String payRange;

        public ResignStatus resignStatus = ResignStatus.NOT_IN_RESIGN_SESSION;
    }

    public static class LaborExportContractRequest {
        public Long contractId;
    }
}
