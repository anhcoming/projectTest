package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.FreelanceContractDTO.*;

public interface FreelanceContractService extends CRUDService<FreelanceContractRequest, FreelanceContractResponse, Long> {
    FileDTO.FileResponse exportContract(Long id);
}
