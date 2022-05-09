package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.CollaboratorContractDTO.*;
import com.viettel.hstd.dto.hstd.ExportInterviewResultDTO;

public interface CollaboratorContractService extends CRUDService<CollaboratorContractRequest, CollaboratorContractResponse, Long> {
    FileDTO.FileResponse exportContract(Long id);
}
