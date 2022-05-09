package com.viettel.hstd.dto.hstd;

import com.viettel.hstd.constant.ContractImportCategory;

import java.time.LocalDate;

public class ContractImportPlanDTO {

    public static class ContractImportPlanRequest {
        public Long contractImportPlanId;
        public ContractImportCategory categoryImport;
        public Integer quarter;
        public Integer year;
        public LocalDate startDate;
        public LocalDate endDate;
        public String fileNameImport;
        public String filePathImport;
    }

    public static class ContractImportPlanResponse {
        public Long contractImportPlanId;
        public ContractImportCategory categoryImport;
        public Integer quarter;
        public Integer year;
        public LocalDate startDate;
        public LocalDate endDate;
        public String fileNameImport;
        public String filePathImport;
    }
}
