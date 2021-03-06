package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.ContractImportPlanDTO;
import com.viettel.hstd.entity.hstd.ContractEntity;
import com.viettel.hstd.entity.hstd.ContractImportPlanEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.ContractImportPlanRepository;
import com.viettel.hstd.service.inf.ContractImportPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ContractImportPlanServiceImp extends BaseService implements ContractImportPlanService {
    @Autowired
    private ContractImportPlanRepository contractImportPlanRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MapUtils mapUtils;

    @Override
    public Page<ContractImportPlanDTO.ContractImportPlanResponse> findPage(SearchDTO searchDTO) {
        Pageable pageable = SearchUtils.getPageable(searchDTO);
        Specification<ContractImportPlanEntity> specs = SearchUtils.getSpecifications(searchDTO);

        Page<ContractImportPlanEntity> list;
        if (searchDTO.pagedFlag) {
            list = contractImportPlanRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = contractImportPlanRepository.findAll(p);
        }

        return list.map(this::convertEntityToResponse);
    }

    @Override
    public ContractImportPlanDTO.ContractImportPlanResponse findOneById(Long id) {
        ContractImportPlanEntity entity = contractImportPlanRepository.findById(id).orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y k??? ho???ch import h???p ?????ng"));
        return convertEntityToResponse(entity);
    }

    @Override
    public Boolean delete(Long id) {
        if (!contractImportPlanRepository.existsById(id)) {
            throw new NotFoundException("Kh??ng t??m th???y k??? ho???ch import h???p ?????ng");
        }
        contractImportPlanRepository.softDelete(id);
        addLog("CONTRACT_IMPORT_PLAN_DELETE");
        return true;
    }

    @Override
    public ContractImportPlanDTO.ContractImportPlanResponse create(ContractImportPlanDTO.ContractImportPlanRequest request) {
        ContractImportPlanEntity entity = objectMapper.convertValue(request, ContractImportPlanEntity.class);
        entity = contractImportPlanRepository.save(entity);
        addLog("CONTRACT_IMPORT_PLAN", "CREATE", new Gson().toJson(request));
        return convertEntityToResponse(entity);
    }

    @Override
    public ContractImportPlanDTO.ContractImportPlanResponse update(Long id, ContractImportPlanDTO.ContractImportPlanRequest request) {
        ContractImportPlanEntity entity = contractImportPlanRepository.findById(id).orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y k??? ho???ch import h???p ?????ng"));
        ContractImportPlanEntity newEntity = objectMapper.convertValue(request, ContractImportPlanEntity.class);
        mapUtils.customMap(newEntity, entity);
        entity.setContractImportPlanId(id);
        entity = contractImportPlanRepository.save(entity);
        addLog("CONTRACT_IMPORT_PLAN", "UPDATE", new Gson().toJson(request));
        return convertEntityToResponse(entity);
    }

    private ContractImportPlanDTO.ContractImportPlanResponse convertEntityToResponse(ContractImportPlanEntity entity) {
        return objectMapper.convertValue(entity, ContractImportPlanDTO.ContractImportPlanResponse.class);
    }

    private boolean validateRequest(ContractImportPlanDTO.ContractImportPlanRequest request) {
        if (request == null) {
            throw new BadRequestException("Kh??ng t??m th???y k??? ho???ch import h???p ?????ng");
        }
        if (request.categoryImport == null) {
            throw new NotFoundException("Kh??ng t??m th???y lo???i h???p ?????ng th???c hi???n k??? ho???ch");
        }
        switch (request.categoryImport) {
            case LABOR:
                if (request.quarter == null || request.year == null) {
                    throw new BadRequestException("Qu?? v?? n??m c???a t??i k?? h???p ?????ng lao ?????ng kh??ng ???????c ????? tr???ng");
                }
                break;
            case PROBATION:
            case PROBATION_TO_LABOR:
                if (request.startDate == null || request.endDate == null) {
                    throw new BadRequestException("Ng??y b???t ?????u  v?? ng??y k???t th??c c???a h???p ?????ng th??? vi???c ho???c chuy???n di???n h???p ?????ng th??? vi???c sang h???p ?????ng lao ?????ng kh??ng ???????c ????? tr???ng");
                }
                break;
            default:
                throw new BadRequestException("Lo???i h???p ?????ng kh??ng ???????c h??? tr??? th???c hi???n k??? ho???ch");
        }

        return true;
    }
}
