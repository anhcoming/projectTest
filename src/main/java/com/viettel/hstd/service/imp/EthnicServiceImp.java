package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EthnicDTO;
import com.viettel.hstd.entity.hstd.EthnicEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.EthnicRepository;
import com.viettel.hstd.service.inf.EthnicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EthnicServiceImp extends BaseService implements EthnicService {

    @Autowired
    EthnicRepository ethnicRepository;

    @Autowired
    Message message;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MapUtils mapUtils;

    @Override
    public EthnicDTO.EthnicResponse findOneById(Long id) {
        EthnicEntity emailConfigEntity = ethnicRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(emailConfigEntity, EthnicDTO.EthnicResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!ethnicRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        ethnicRepository.softDelete(id);
        addLog("EMAIL_CONFIG");
        return true;
    }

    @Override
    public Page<EthnicDTO.EthnicResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<EthnicEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<EthnicEntity> list;
        if (searchRequest.pagedFlag) {
            list = ethnicRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = ethnicRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, EthnicDTO.EthnicResponse.class)
        );
    }

    @Override
    public EthnicDTO.EthnicResponse update(Long id, EthnicDTO.EthnicRequest request) {
        EthnicEntity emailConfigEntity = ethnicRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        EthnicEntity newE = objectMapper.convertValue(request, EthnicEntity.class);
        mapUtils.customMap(newE, emailConfigEntity);
        emailConfigEntity.setEthnicId(id);
        emailConfigEntity = ethnicRepository.save(emailConfigEntity);
        addLog("UPDATE");
        return objectMapper.convertValue(emailConfigEntity, EthnicDTO.EthnicResponse.class);
    }

    @Override
    public boolean check(String content) {
        EthnicEntity entity = ethnicRepository.findById(166l).orElse(new EthnicEntity());
        entity.setDescription(content);
        ethnicRepository.save(entity);
        return true;
    }
}
