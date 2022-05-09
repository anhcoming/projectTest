package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.SysConfigDTO.*;
import com.viettel.hstd.entity.hstd.SysConfigEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.SysConfigRepository;
import com.viettel.hstd.service.inf.SysConfigService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SysConfigServiceImp extends BaseService implements SysConfigService {

    @Autowired
    private SysConfigRepository sysConfigRepository;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;

    @Override
    public Page<SysConfigResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<SysConfigEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<SysConfigEntity> list;
        if (searchRequest.pagedFlag) {
            list = sysConfigRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = sysConfigRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, SysConfigResponse.class)
        );
    }

    @Override
    public SysConfigResponse findOneById(Long id) {
        SysConfigEntity entity = sysConfigRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(entity, SysConfigResponse.class);
    }

    @Override
    public SysConfigResponse findOneByKey(String key) {
        SysConfigEntity entity = sysConfigRepository
                .findFirstByConfigKey(key)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(entity, SysConfigResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!sysConfigRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        sysConfigRepository.softDelete(id);
        super.addLog("SYS_CONFIG", "DELETE", id.toString());
        return true;
    }

    @Override
    public SysConfigResponse create(SysConfigRequest request) {
        SysConfigEntity entity = objectMapper.convertValue(request, SysConfigEntity.class);
        entity = sysConfigRepository.save(entity);
        super.addLog("SYS_CONFIG", "CREATE", new Gson().toJson(request));
        return objectMapper.convertValue(entity, SysConfigResponse.class);
    }

    @Override
    public SysConfigResponse update(Long id, SysConfigRequest request) {
        SysConfigEntity entity = sysConfigRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        SysConfigEntity newE = objectMapper.convertValue(request, SysConfigEntity.class);
        mapUtils.customMap(newE, entity);
        entity.setSysConfigId(id);
        entity = sysConfigRepository.save(entity);
        super.addLog("SYS_CONFIG", "UPDATE", new Gson().toJson(request));
        return objectMapper.convertValue(entity, SysConfigResponse.class);
    }
}
