package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.InsuranceStatus;
import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.InsuranceSessionDTO.*;
import com.viettel.hstd.entity.hstd.InsuranceSessionEntity;
import com.viettel.hstd.entity.hstd.InsuranceTerminateEntity;
import com.viettel.hstd.entity.hstd.ResignSessionEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.filter.HSTDFilter;
import com.viettel.hstd.repository.hstd.InsuranceSessionRepository;
import com.viettel.hstd.service.inf.InsuranceSessionService;
import com.viettel.hstd.service.inf.InsuranceTerminateService;
import com.viettel.hstd.service.inf.TerminateContractService;
import liquibase.pro.packaged.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class InsuranceSessionImp implements InsuranceSessionService {

    private final InsuranceSessionRepository insuranceSessionRepository;
    private final ObjectMapper objectMapper;
    private final MapUtils mapUtils;
    private final HSTDFilter hstdFilter;
    private final TerminateContractService terminateContractService;
    private final InsuranceTerminateService insuranceTerminateService;


    public InsuranceSessionImp(InsuranceSessionRepository insuranceSessionRepository, ObjectMapper objectMapper, MapUtils mapUtils, HSTDFilter hstdFilter, TerminateContractService terminateContractService, InsuranceTerminateService insuranceTerminateService) {
        this.insuranceSessionRepository = insuranceSessionRepository;
        this.objectMapper = objectMapper;
        this.mapUtils = mapUtils;
        this.hstdFilter = hstdFilter;
        this.terminateContractService = terminateContractService;
        this.insuranceTerminateService = insuranceTerminateService;
    }

    @Override
    public Page<InsuranceSessionResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<InsuranceSessionEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<InsuranceSessionEntity> page;
        if (searchRequest.pagedFlag) {
            page = insuranceSessionRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            page = insuranceSessionRepository.findAll(p);
        }

        return page.map(this::convertEntity2Response);
    }

    @Override
    public InsuranceSessionResponse findOneById(Long id) {
        InsuranceSessionEntity resignSessionEntity = insuranceSessionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y ?????t ch???t s???"));

        return convertEntity2Response(resignSessionEntity);
    }

    @Override
    public Boolean delete(Long id) {
        InsuranceSessionEntity insuranceSessionEntity = insuranceSessionRepository.findById(id).orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y ?????t t??i k??"));
        Set<Long> contractIdSet = insuranceSessionEntity.getInsuranceTerminateEntityList().stream().map(insuranceTerminateEntity -> insuranceTerminateEntity.getTerminateContractEntity().getContractId()).collect(Collectors.toSet());
        terminateContractService.updateInsuranceStatus(contractIdSet, InsuranceStatus.NOT_IN_INSURANCE_PROGRESS);
        insuranceSessionEntity.getInsuranceTerminateEntityList().clear();
        insuranceSessionRepository.save(insuranceSessionEntity);
        insuranceSessionRepository.delete(insuranceSessionEntity);
        return true;
    }

    @Override
    public InsuranceSessionResponse create(InsuranceSessionRequest request) {
        InsuranceSessionEntity insuranceSessionEntity = objectMapper.convertValue(request, InsuranceSessionEntity.class);

        insuranceSessionEntity.setFinishInsuranceProcedureTimestamp(null);
        insuranceSessionEntity.setDoneAddingContractTimestamp(null);
        insuranceSessionEntity.setDoneDecreasingAndPrepareDocumentTimestamp(null);
        insuranceSessionEntity.setDoneSendingToBhxhTimestamp(null);
        insuranceSessionEntity.setInsuranceStatus(InsuranceStatus.JUST_ADD_INTO_INSURANCE_PROGRESS);

        insuranceSessionEntity = insuranceSessionRepository.save(insuranceSessionEntity);
        return convertEntity2Response(insuranceSessionEntity);
    }

    @Override
    public InsuranceSessionResponse update(Long id, InsuranceSessionRequest request) {
        InsuranceSessionEntity insuranceSessionEntity = insuranceSessionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y ?????t ch???t s???"));

        if (insuranceSessionEntity.getInsuranceStatus().getValue() > InsuranceStatus.DONE_ADD_INTO_INSURANCE_PROGRESS.getValue()) {
            throw new BadRequestException("Kh??ng th??? th??m h???p ?????ng do ???? ch???t h???p ?????ng");
        }

         Set<Long> oldTerminateIds = insuranceSessionEntity.getInsuranceTerminateEntityList()
                 .stream()
                 .map(obj -> obj.getTerminateContractEntity().getTerminateContractId())
                 .collect(Collectors.toSet());

        Set<Long> newTerminateIds = new HashSet<>(request.getTerminateIds());

        Set<Long> terminateIdNeed2BeRemove = oldTerminateIds.stream().filter(obj -> !newTerminateIds.contains(obj)).collect(Collectors.toSet());
        Set<Long> terminateIdNeed2BeAdded = newTerminateIds.stream().filter(obj -> !oldTerminateIds.contains(obj)).collect(Collectors.toSet());

        insuranceTerminateService.addContractToInsuranceSession(terminateIdNeed2BeAdded, insuranceSessionEntity.getInsuranceSessionId());
        insuranceTerminateService.removeContractToInsuranceSession(terminateIdNeed2BeRemove, insuranceSessionEntity.getInsuranceSessionId());

        InsuranceSessionEntity newInsuranceSessionEntity = objectMapper.convertValue(request, InsuranceSessionEntity.class);
        mapUtils.customMap(newInsuranceSessionEntity, insuranceSessionEntity);
        insuranceSessionEntity.setInsuranceSessionId(id);

        return InsuranceSessionService.super.update(id, request);
    }

    private InsuranceSessionResponse convertEntity2Response(InsuranceSessionEntity insuranceSessionEntity) {
        InsuranceSessionResponse insuranceSessionResponse = objectMapper.convertValue(insuranceSessionEntity, InsuranceSessionResponse.class);

        return insuranceSessionResponse;
    }

    @Override
    public void finishAddingContractToInsuranceSession(Long insuranceSessionId) {
        InsuranceSessionEntity insuranceSessionEntity = insuranceSessionRepository.findById(insuranceSessionId)
                .orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y ?????t ch???t s???"));
        if (!insuranceSessionEntity.getInsuranceStatus().equals(InsuranceStatus.JUST_ADD_INTO_INSURANCE_PROGRESS)) {
            throw new BadRequestException("?????t ch???t s??? n??y kh??ng n???m trong n???m trong tr???ng th??i ??ang th??m h???p ?????ng n??n kh??ng k???t th??c qu?? tr??nh th??m ???????c");
        }
        insuranceSessionEntity.setInsuranceStatus(InsuranceStatus.DONE_ADD_INTO_INSURANCE_PROGRESS);
        insuranceSessionRepository.save(insuranceSessionEntity);
    }
}
