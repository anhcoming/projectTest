package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;
import com.viettel.hstd.constant.VPSConstant;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmployerInfoDTO.*;
import com.viettel.hstd.entity.hstd.EmployerInfoEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.AccessDeniedException;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.filter.HSTDFilter;
import com.viettel.hstd.repository.hstd.EmployerInfoRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.EmployerInforService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;

@Service
public class EmployerInforServiceImp extends BaseService implements EmployerInforService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;
    @Autowired
    private EmployerInfoRepository employerInfoRepository;

    @Autowired
    private VhrFutureOrganizationRepository organizationRepository;

    @Autowired
    HSTDFilter hstdFilter;

    @Override
    public Page<EmployerInfoResponse> findPage(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "unitId");
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<EmployerInfoEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<EmployerInfoEntity> list;
        if (searchRequest.pagedFlag) {
            list = employerInfoRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = employerInfoRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, EmployerInfoResponse.class)
        );
    }

    @Override
    public EmployerInfoResponse findOneById(Long id) {
        EmployerInfoEntity emailConfigEntity = employerInfoRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(emailConfigEntity, EmployerInfoResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        if (sSoResponse.getOrganizationId() == null) {
            throw new AccessDeniedException("Bạn không có quyền xóa đại diện đơn vị");
        }

        EmployerInfoEntity entity = employerInfoRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đại diện đơn vị"));

        if (!sSoResponse.getUnitId().equals(entity.getUnitId()) && !sSoResponse.getUnitId().equals(VPSConstant.ALL_PROVINCE_UNIT_ID)) {
            throw new AccessDeniedException("Bạn không có quyền xóa đại diện đơn vị " + entity.getUnitName());
        }

        employerInfoRepository.softDelete(id);

        return true;
    }


    @Override
    public EmployerInfoResponse create(EmployerInfoRequest request) {
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        if (sSoResponse.getOrganizationId() == null) {
            throw new AccessDeniedException("Bạn không có quyền tạo đại diện đơn vị");
        }
        // Check duplicate
        if (request.unitId != null) {
            if (employerInfoRepository.existsByUnitId(request.unitId)) {
                throw new BadRequestException("Đã tồn tại người đại diện của đơn vị này (" + request.unitName + ")");
            }
            VhrFutureOrganizationEntity organizationEntity = organizationRepository.findById(request.unitId)
                    .orElse(null);
            if (organizationEntity == null) {
                throw new NotFoundException("Đơn vị tạo ra không được tìm thấy trên SSO");
            }
            if (!request.unitId.equals(sSoResponse.getUnitId()) && !sSoResponse.getUnitId().equals(VPSConstant.ALL_PROVINCE_UNIT_ID)) {
                throw new BadRequestException("Bạn không có quyền tạo đại diện đơn vị " + request.unitName);
            }

            EmployerInfoEntity entity = objectMapper.convertValue(request, EmployerInfoEntity.class);
            entity = employerInfoRepository.save(entity);

            addLog("EMPLOYER_INFO", "CREATE", new Gson().toJson(request));
            return objectMapper.convertValue(entity, EmployerInfoResponse.class);
        } else {
            throw new NotFoundException("Trường đơn vị là bắt buộc");
        }
    }

    @Override
    public EmployerInfoResponse update(Long id, EmployerInfoRequest request) {
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        if (sSoResponse.getOrganizationId() == null) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật đại diện đơn vị");
        }
        EmployerInfoEntity entity = employerInfoRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đại diện đơn vị"));

        if (!request.unitId.equals(entity.getUnitId())) {
            throw new BadRequestException("Không được sửa đơn vị của đại diện đơn vị");
        }

        if (!sSoResponse.getUnitId().equals(entity.getUnitId()) && !sSoResponse.getUnitId().equals(VPSConstant.ALL_PROVINCE_UNIT_ID)) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật đại diện đơn vị " + entity.getUnitName());
        }

        EmployerInfoEntity newE = objectMapper.convertValue(request, EmployerInfoEntity.class);
        mapUtils.customMap(newE, entity);
        entity.setEmployerInfoId(id);
        entity = employerInfoRepository.save(entity);
        addLog("EMPLOYER_INFO", "UPDATE", new Gson().toJson(request));
        return objectMapper.convertValue(entity, EmployerInfoResponse.class);
    }

    @Override
    public EmployerInfoResponseForExport convertEntityToExportResponse(EmployerInfoEntity entity) {
        EmployerInfoResponseForExport response = new EmployerInfoResponseForExport();
        response.employerInfoId = entity.getEmployerInfoId();
        response.employerAddress = entity.getAddress();
        response.employerUnitName = entity.getUnitName();
        response.employerUnitId = entity.getUnitId();
        response.employerPhoneNumber = entity.getPhoneNumber();
        response.employerTaxCode = entity.getTaxCode();
        response.employerAuthorizedNo = entity.getAuthorizedNo();
        response.employerAuthorizedDate = entity.getAuthorizedDate();
        response.employerRepresentative = entity.getRepresentative();
        response.employerPosition = entity.getPosition();

        response.employerRepresentativeNationality = entity.getRepresentativeNationality();
        response.employerRepresentativePersonalIdNumber = entity.getRepresentativePersonalIdNumber();
        response.employerRepresentativePersonalIdIssueDate = entity.getRepresentativePersonalIdIssueDate();
        response.employerRepresentativePersonalIdIssuePlace = entity.getRepresentativePersonalIdIssuePlace();
        response.employerRepresentativeDateOfBirth = entity.getRepresentativeDateOfBirth();
        response.employerRepresentativePlaceOfBirth = entity.getRepresentativePlaceOfBirth();
        response.employerRepresentativeAddress = entity.getRepresentativeAddress();

        return response;
    }
}
