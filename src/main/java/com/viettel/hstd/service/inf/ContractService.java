package com.viettel.hstd.service.inf;

import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ContractDTO.*;
import com.viettel.hstd.dto.hstd.ContractImportDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface ContractService extends CRUDService<ContractRequest, ContractResponse, Long> {
    void updateVofficeCalled(Long id, ContractRequest request);

    ContractResponse findByTransCode(String transCode);

    void updateSignedFile(String filePath, Long id);

    ContractResponse updateContractFile(String fileName, Long id);

    void updateContractResignStatus(Long resignSessionId, ResignStatus resignStatus);

    void updateTranscodeOfContractInResignSession(String transcode, Long resignSessionId);

    boolean updateContractResignStatus(UpdateResignStatusRequest request);

    boolean updateNewContractStatus(ContractNewStatusRequest request);

    boolean updateTransCode(UpdateTransCodeRequest request);

    boolean updateSignedFile(ContractChangeSignedFileRequest request);

    boolean updateResignStatus(Long contractId, ResignStatus resignStatus);

    boolean updateResignStatus(Set<Long> contractIdSet, ResignStatus resignStatus);

    void updateContractFileAndContractFileEncodePath(String fileName, String encodePath, Long id);

    void updateContractFileEncodeToContractFile(Set<Long> contractIdSet);

    FileDTO.FileResponse exportDanhSachNhanSu();

    String getManagerSign(Long unitId, String unitName);
}
