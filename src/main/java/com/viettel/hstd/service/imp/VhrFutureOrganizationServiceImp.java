package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.VPSConstant;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.core.utils.StringUtils;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.service.inf.VhrFutureOrganizationService;
import com.viettel.hstd.dto.vps.VhrFutureOrganizationDTO.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VhrFutureOrganizationServiceImp extends BaseService implements VhrFutureOrganizationService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;
    @Autowired
    private VhrFutureOrganizationRepository organizationRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Override

    public Page<VhrFutureOrganizationResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<VhrFutureOrganizationEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<VhrFutureOrganizationEntity> list;
        if (searchRequest.pagedFlag) {
            list = organizationRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = organizationRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, VhrFutureOrganizationResponse.class)
        );
    }

    @Override
    public VhrFutureOrganizationResponse findOneById(Long id) {
        VhrFutureOrganizationEntity organizationEntity = organizationRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(organizationEntity, VhrFutureOrganizationResponse.class);
    }

    @Override
    public Boolean delete(Long aLong) {
        return false;
    }

    @Override
    public DepartmentUnitResponse getDepartmentAndUnitFromOrganization(Long organizationId) {
        DepartmentUnitResponse response = new DepartmentUnitResponse();

        VhrFutureOrganizationEntity organizationEntity = organizationRepository
            .findById(organizationId)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng ban đơn vị"));

        String unitIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.UNIT_ORGANIZATION_LEVEL);
        String departmentIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.DEPARTMENT_ORGANIZATION_LEVEL);

        if (unitIdString == null || departmentIdString == null) {
            throw new NotFoundException("Không tìm thấy phòng ban đơn vị");
        }
        try {
            response.unitId = Long.parseLong(unitIdString);
            response.unitName = organizationEntity.getOrgNameLevel2();
            response.departmentId = Long.parseLong(departmentIdString);
            response.departmentName = organizationEntity.getOrgNameLevel3();
        } catch (NumberFormatException ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }

        return response;
    }

    @Override
    public DepartmentUnitResponse getOnlyUnitFromOrganization(Long organizationId) {
        DepartmentUnitResponse response = new DepartmentUnitResponse();

        VhrFutureOrganizationEntity organizationEntity = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng ban đơn vị"));


        String unitIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.UNIT_ORGANIZATION_LEVEL);

        if (unitIdString == null) {
            throw new NotFoundException("Không tìm thấy đơn vị");
        }
        try {
            response.unitId = Long.parseLong(unitIdString);
            response.unitName = organizationEntity.getOrgNameLevel2();
        } catch (NumberFormatException ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }

        return response;
    }

}
