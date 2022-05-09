package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.vps.VhrFutureOrganizationDTO.*;

public interface VhrFutureOrganizationService extends CRUDService<VhrFutureOrganizationRequest, VhrFutureOrganizationResponse, Long> {
    DepartmentUnitResponse getDepartmentAndUnitFromOrganization(Long organizationId);
    DepartmentUnitResponse getOnlyUnitFromOrganization(Long organizationId);
}
