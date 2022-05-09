package com.viettel.hstd.controller;

import com.viettel.hstd.constant.Operation;
import com.viettel.hstd.constant.SearchType.*;
import com.viettel.hstd.constant.VPSConstant;
import com.viettel.hstd.core.constant.ConstantConfig;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.dto.SearchDTO.*;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.vps.VhrFutureOrganizationDTO.*;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.VhrFutureOrganizationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.viettel.hstd.constant.SearchType.NUMBER;
import static com.viettel.hstd.constant.SearchType.STRING;
import static com.viettel.hstd.constant.VPSConstant.*;

@RestController()
@RequestMapping("organization")
@Tag(name = "Organization")
public class VhrFutureOrganizationController {

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    private VhrFutureOrganizationService positionService;

    @GetMapping
    public BaseResponse<List<VhrFutureOrganizationResponse>> findAll() {
        return SearchUtils.getResponseFromPage(positionService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<VhrFutureOrganizationResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(positionService.findPage(searchDTO));
    }

    @GetMapping("/{id}")
    public BaseResponse<VhrFutureOrganizationResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<VhrFutureOrganizationResponse>()
                .success(positionService.findOneById(id));
    }

    @PostMapping("/unit/search")
    public BaseResponse<List<VhrFutureOrganizationResponse>> searchUnit(@RequestBody SearchDTO searchDTO) {
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        Long unitId = sSoResponse.getUnitId();

        List<SearchCriteria> oldSearchFilter = searchDTO.criteriaList;
        List<SearchCriteria> newSearchFilter = new ArrayList<>();

        SearchCriteria level2Filter = new SearchCriteria();
        level2Filter.setField("orgLevel");
        level2Filter.setOperation(Operation.EQUAL);
        level2Filter.setType(NUMBER);
        level2Filter.setValue(5l);
        newSearchFilter.add(level2Filter);

        SearchCriteria notTTKTFilter = new SearchCriteria();
        notTTKTFilter.setField("name");
        notTTKTFilter.setOperation(Operation.NOT_LIKE);
        notTTKTFilter.setType(STRING);
        notTTKTFilter.setValue("Trung tâm Kỹ thuật Viettel");
        newSearchFilter.add(notTTKTFilter);

        if (!(unitId.equals(ALL_PROVINCE_UNIT_ID) || sSoResponse.getRoleSet().contains(HSTD_ADMIN_ROLE_CODE))) {
            SearchCriteria unitFilter = new SearchCriteria();
            unitFilter.setField("path");
            unitFilter.setOperation(Operation.LIKE);
            unitFilter.setType(NUMBER);
            unitFilter.setValue(unitId);
            newSearchFilter.add(unitFilter);
        }

        for (int i = 0; i < oldSearchFilter.size(); i++) {
            if (!oldSearchFilter.get(i).getAndFlag()) {
                for (int j = 0; j < newSearchFilter.size(); j++) {
                    oldSearchFilter.add(i + 1, newSearchFilter.get(j));
                }
            }
        }

        searchDTO.criteriaList = oldSearchFilter.size() == 0 ? newSearchFilter : oldSearchFilter;

        return SearchUtils.getResponseFromPage(positionService.findPage(searchDTO));
    }

    @PostMapping("/department/search")
    public BaseResponse<List<VhrFutureOrganizationResponse>> searchDepartment(@RequestBody SearchDTO searchDTO) {
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        Long unitId = sSoResponse.getUnitId();

        List<SearchCriteria> oldSearchFilter = searchDTO.criteriaList;
        List<SearchCriteria> newSearchFilter = new ArrayList<>();

        SearchCriteria level3Filter = new SearchCriteria();
        level3Filter.setField("orgLevel");
        level3Filter.setOperation(Operation.EQUAL);
        level3Filter.setType(NUMBER);
        level3Filter.setValue(6l);
        newSearchFilter.add(level3Filter);

        if (!unitId.equals(ALL_PROVINCE_UNIT_ID)) {
            SearchCriteria unitFilter = new SearchCriteria();
            unitFilter.setField("path");
            unitFilter.setOperation(Operation.LIKE);
            unitFilter.setType(NUMBER);
            unitFilter.setValue(unitId);
            newSearchFilter.add(unitFilter);
        }

        for (int i = 0; i < oldSearchFilter.size(); i++) {
            if (!oldSearchFilter.get(i).getAndFlag()) {
                for (int j = 0; j < newSearchFilter.size(); j++) {
                    oldSearchFilter.add(i + 1, newSearchFilter.get(j));
                }
            }
        }

        searchDTO.criteriaList = oldSearchFilter.size() == 0 ? newSearchFilter : oldSearchFilter;

        return SearchUtils.getResponseFromPage(positionService.findPage(searchDTO));
    }

}
