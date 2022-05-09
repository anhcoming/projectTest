package com.viettel.hstd.service.inf;

import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ContractDTO;
import com.viettel.hstd.dto.hstd.LaborContractDTO;
import com.viettel.hstd.entity.hstd.ContractEntity;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface LaborContractService extends CRUDService<LaborContractDTO.LaborContractRequest, LaborContractDTO.LaborContractResponse, Long> {
    Page<LaborContractDTO.LaborContractResponse> findAboutToExpiredContract(SearchDTO searchRequest);

    void updateContractResignStatus(Set<Long> contractIds, ResignStatus resignStatus);

    FileDTO.FileResponse addEmployeeSignatureInResign(ContractDTO.ContractAddEmployeeSignature request);

    FileDTO.FileResponse sendContractToEmployeeInResign(Long contractId);

    boolean updateResignStatus(Long contractId, ResignStatus resignStatus);

    void turnTempContractToActualContract(Set<Long> contractIdSet);

    FileDTO.FileResponse createTempContract(ContractDTO.ContractExportRequest contractExportRequest);

    FileDTO.FileResponse createOnlyTempContract(ContractEntity contractEntity);
}
