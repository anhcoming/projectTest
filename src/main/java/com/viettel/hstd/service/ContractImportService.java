package com.viettel.hstd.service;

import com.viettel.hstd.dto.hstd.ContractImportDTO;
import com.viettel.hstd.security.sso.SSoResponse;

public interface ContractImportService {
    ContractImportDTO.ContractImportResponse importContractFromExcel(SSoResponse soResponse, ContractImportDTO.ContractImportRequest request);

    Boolean deleteContractImport(String employeeCode);
}
