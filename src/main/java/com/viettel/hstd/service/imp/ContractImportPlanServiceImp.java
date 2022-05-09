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
        ContractImportPlanEntity entity = contractImportPlanRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy kế hoạch import hợp đồng"));
        return convertEntityToResponse(entity);
    }

    @Override
    public Boolean delete(Long id) {
        if (!contractImportPlanRepository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy kế hoạch import hợp đồng");
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
        ContractImportPlanEntity entity = contractImportPlanRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy kế hoạch import hợp đồng"));
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
            throw new BadRequestException("Không tìm thấy kế hoạch import hợp đồng");
        }
        if (request.categoryImport == null) {
            throw new NotFoundException("Không tìm thấy loại hợp đồng thực hiện kế hoạch");
        }
        switch (request.categoryImport) {
            case LABOR:
                if (request.quarter == null || request.year == null) {
                    throw new BadRequestException("Quý và năm của tái ký hợp đồng lao động không được để trống");
                }
                break;
            case PROBATION:
            case PROBATION_TO_LABOR:
                if (request.startDate == null || request.endDate == null) {
                    throw new BadRequestException("Ngày bắt đầu  và ngày kết thúc của hợp đồng thử việc hoặc chuyển diện hợp đồng thử việc sang hợp đồng lao động không được để trống");
                }
                break;
            default:
                throw new BadRequestException("Loại hợp đồng không được hỗ trợ thực hiện kế hoạch");
        }

        return true;
    }
}
