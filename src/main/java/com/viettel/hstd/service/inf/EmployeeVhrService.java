package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.EmployeeVhrTempDTO;
import com.viettel.hstd.dto.vps.EmployeeVhrDTO.*;
import org.springframework.data.domain.Page;

public interface EmployeeVhrService extends CRUDService<EmployeeVhrRequest, EmployeeVhrResponse, Long> {
    EmployeeVhrTempDTO.EmployeeVhrTempResponse getProfile();
    Page<EmployeeFullResponse> findFullPage(SearchDTO searchRequest);

    EmployeeVhrResponse findOneByIdCombineDb(Long id);

    Integer countByPositionIdAndOrganizationId(Long positionId, Long organizationId);
}
