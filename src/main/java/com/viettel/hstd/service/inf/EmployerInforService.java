package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.EmployerInfoDTO.*;
import com.viettel.hstd.entity.hstd.EmployerInfoEntity;

public interface EmployerInforService extends CRUDService<EmployerInfoRequest, EmployerInfoResponse, Long> {
    EmployerInfoResponseForExport convertEntityToExportResponse(EmployerInfoEntity entity);
}
