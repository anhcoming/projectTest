package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.Operation;
import com.viettel.hstd.constant.SearchType;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.ConstantConfig;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmailCategoryDTO;
import com.viettel.hstd.dto.vps.DomainDataDTO;
import com.viettel.hstd.entity.hstd.EmailCategoryEntity;
import com.viettel.hstd.entity.vps.DomainDataEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.vps.DomainDataRepository;
import com.viettel.hstd.service.inf.DomainDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class DomainDataServiceImp implements DomainDataService {
    @Autowired
    private DomainDataRepository domainDataRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Message message;

    @Override
    public Page<DomainDataDTO.DomainDataResponse> findPage(SearchDTO searchRequest) {
        if (searchRequest == null) {
            searchRequest = new SearchDTO();
            searchRequest.size = 100;
            searchRequest.page = 0;
            searchRequest.pagedFlag = true;
        }
        if (searchRequest.criteriaList == null) {
            searchRequest.criteriaList = new ArrayList<>();
        }
        //<editor-fold desc="set default criteria">
        SearchDTO.SearchCriteria criteria = new SearchDTO.SearchCriteria();
        if (!searchRequest.criteriaList.stream().anyMatch(x -> x.getField().equalsIgnoreCase("isActive"))) {
            criteria = new SearchDTO.SearchCriteria();
            criteria.setField("isActive");
            criteria.setOperation(Operation.EQUAL);
            criteria.setType(SearchType.BOOLEAN);
            criteria.setValue("true");
            searchRequest.criteriaList.add(criteria);
        }
        if (!searchRequest.criteriaList.stream().anyMatch(x -> x.getField().equalsIgnoreCase("domainTypeId"))) {
            criteria = new SearchDTO.SearchCriteria();
            criteria.setField("domainTypeId");
            criteria.setOperation(Operation.EQUAL);
            criteria.setType(SearchType.NUMBER);
            criteria.setValue(ConstantConfig.defaultDomainData);
            searchRequest.criteriaList.add(criteria);
        }
        //</editor-fold>
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<DomainDataEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<DomainDataEntity> list;
        if (searchRequest.pagedFlag) {
            list = domainDataRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = domainDataRepository.findAll(p);
        }
        return list.map(obj ->
                this.objectMapper.convertValue(obj, DomainDataDTO.DomainDataResponse.class)
        );
    }

    @Override
    public DomainDataDTO.DomainDataResponse findOneById(Long id) {
        DomainDataEntity emailConfigEntity = domainDataRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(emailConfigEntity, DomainDataDTO.DomainDataResponse.class);
    }

    @Override
    public Boolean delete(Long aLong) {
        return null;
    }
}
