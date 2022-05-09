package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.InsuranceSessionDTO.*;

public interface InsuranceSessionService extends CRUDService<InsuranceSessionRequest, InsuranceSessionResponse, Long> {
    void finishAddingContractToInsuranceSession(Long insuranceSessionId);
}
