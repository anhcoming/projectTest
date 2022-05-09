package com.viettel.hstd.service.inf;

import com.viettel.hstd.constant.InsuranceStatus;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.TerminateContractDTO.*;
import com.viettel.hstd.entity.hstd.TerminateContractEntity;

import java.util.List;
import java.util.Set;

public interface TerminateContractService extends CRUDService<TerminateContractRequest, TerminateContractResponse, Long> {
    void updateStatus(String transCode, Long id, Integer status, String filePath, String debtFile, String agrFile);

    TerminateContractResponse findByTransCode(String transCode);

    /**
     * Cap nhat file trinh ky don xin nghi viec
     *
     * @param fileName
     * @param id
     */
    void updateSignedFile(String fileName, Long id, Integer status, String documentCode, String debtFile, String agrFile);

    TerminateContractResponse updateDecision(TerminateContractRequest request);

    FileDTO.FileResponse createSevAllowance(TerminateContractRequest request);

    TerminateContractResponse confrimQuit(TerminateContractRequest request);

    void updateFile(String filePath, Long id, Integer type);

    FileDTO.FileResponse exportExcel(SearchDTO searchRequest);

    FileDTO.FileResponse exportIdleTime(SearchDTO searchRequest);

    TerminateContractResponse sendResignation(TerminateContractRequest request);

    void updateInsuranceStatus(Set<Long> terminateIds, InsuranceStatus insuranceStatus);

    TerminateContractResponse findByTransCodeSevAllowance(String transCode);

    FileDTO.FileResponse mergeTerminateContract(List<Long> ids);

    FileDTO.FileResponse mergeTerminateContractOnlyPdf(List<Long> ids);

    List<TerminateContractEntity> findBySevAllowanceMulti(String transCode);

    List<TerminateContractEntity> saveSevAllowanceMulti(boolean isSuccess, List<TerminateContractEntity> terminateContractEntities, String filePath);
}
