package com.viettel.hstd.filter;

import com.viettel.hstd.constant.Operation;
import com.viettel.hstd.constant.SearchType;
import com.viettel.hstd.constant.VPSConstant;
import com.viettel.hstd.core.constant.ConstantConfig;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.viettel.hstd.constant.VPSConstant.HSTD_ADMIN_ROLE_CODE;

@Component
public class HSTDFilter {
    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    VhrFutureOrganizationRepository organizationRepository;

    public void unitDepartmentFilter(SearchDTO searchRequest, String field) {
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();

        if (sSoResponse.getUnitId() == null) return;
        Long unitId = sSoResponse.getUnitId();

        if (unitId.equals(VPSConstant.ALL_PROVINCE_UNIT_ID) ||  sSoResponse.getRoleSet().contains(HSTD_ADMIN_ROLE_CODE)) return;

        SearchDTO.SearchCriteria criteria = new SearchDTO.SearchCriteria();
        criteria.setField(field);
        criteria.setType(SearchType.NUMBER);
        criteria.setOperation(Operation.EQUAL);
        criteria.setValue(unitId);

        searchRequest.criteriaList.add(criteria);
    }
}
