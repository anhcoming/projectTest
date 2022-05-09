package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ContractDTO;
import com.viettel.hstd.dto.hstd.ProbationaryContractDTO.*;
import com.viettel.hstd.entity.hstd.ProbationaryContractEntity;

public interface ProbationaryContractService extends CRUDService<ProbationaryContractRequest, ProbationaryContractResponse, Long> {
    FileDTO.FileResponse exportContract(ProbationaryExportContractRequest request);

    boolean updateNewContractStatus(ProbationaryNewStatusRequest request);
    FileDTO.FileResponse addEmployeeSignatureToContract(ContractDTO.ContractAddEmployeeSignature request);

    FileDTO.FileResponse sendContractToEmployeeInNewContract(Long contractId);

    FileDTO.FileResponse createContractFileFromEntity(ContractDTO.ContractExportRequest request);

    FileDTO.FileResponse onlyExportContract(ProbationaryContractEntity contract);
}