package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.SysConfigDTO;
import com.viettel.hstd.dto.hstd.SysLogDTO;
import com.viettel.hstd.entity.hstd.SysConfigEntity;
import com.viettel.hstd.entity.hstd.SysLogEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.SysLogRepository;
import com.viettel.hstd.service.inf.SysLogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class SysLogServiceImp implements SysLogService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;

    @Autowired
    private SysLogRepository sysLogRepository;

    @Override
    public Page<SysLogDTO.SysLogResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<SysLogEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<SysLogEntity> list;
        if (searchRequest.pagedFlag) {
            list = sysLogRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = sysLogRepository.findAll(p);
        }
        this.objectMapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));
        return list.map(obj ->
                this.objectMapper.convertValue(obj, SysLogDTO.SysLogResponse.class)
        );
    }

    @Override
    public SysLogDTO.SysLogResponse findOneById(Long id) {
        SysLogEntity entity = sysLogRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(entity, SysLogDTO.SysLogResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!sysLogRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        sysLogRepository.softDelete(id);
        return true;
    }


    public void create(SysLogEntity request) {
        sysLogRepository.save(request);
    }
}
