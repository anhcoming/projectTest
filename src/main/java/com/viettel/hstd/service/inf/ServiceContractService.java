package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ServiceContractDTO.*;


public interface ServiceContractService extends CRUDService<ServiceContractRequest, ServiceContractResponse, Long> {
    FileDTO.FileResponse exportContract(Long id);
}
