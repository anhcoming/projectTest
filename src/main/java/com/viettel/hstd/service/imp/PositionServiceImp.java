package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.service.inf.PositionService;
import com.viettel.hstd.dto.vps.PositionDTO.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PositionServiceImp extends BaseService implements PositionService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;
    @Autowired
    private PositionRepository positionRepository;

    @Override

    public Page<PositionResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<PositionEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<PositionEntity> list;
        if (searchRequest.pagedFlag) {
            list = positionRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = positionRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, PositionResponse.class)
        );
    }

    @Override
    public PositionResponse findOneById(Long id) {
        PositionEntity positionEntity = positionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(positionEntity, PositionResponse.class);
    }

    @Override
    public Boolean delete(Long aLong) {
        return false;
    }
}
