package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmailTemplateDTO.*;
import com.viettel.hstd.dto.hstd.InterviewSessionCvDTO;
import com.viettel.hstd.entity.hstd.EmailTemplateEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.EmailTemplateRepository;
import com.viettel.hstd.service.inf.EmailTemplateService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailTemplateServiceImp extends BaseService implements EmailTemplateService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Override
    public Page<EmailTemplateResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<EmailTemplateEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<EmailTemplateEntity> list;
        if (searchRequest.pagedFlag) {
            list = emailTemplateRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = emailTemplateRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, EmailTemplateResponse.class)
        );
    }

    @Override
    public EmailTemplateResponse findOneById(Long id) {
        EmailTemplateEntity entity = emailTemplateRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(entity, EmailTemplateResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!emailTemplateRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        emailTemplateRepository.softDelete(id);
        addLog("EMAIL_TEMPLATE", "DELETE", id.toString());
        return true;
    }

    @Override
    public EmailTemplateResponse create(EmailTemplateRequest request) {
        EmailTemplateEntity entity = objectMapper.convertValue(request, EmailTemplateEntity.class);
        entity = emailTemplateRepository.save(entity);
        addLog("EMAIL_TEMPLATE", "CREATE", new Gson().toJson(request));
        return objectMapper.convertValue(entity, EmailTemplateResponse.class);
    }

    @Override
    public EmailTemplateResponse update(Long id, EmailTemplateRequest request) {
        EmailTemplateEntity entity = emailTemplateRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        EmailTemplateEntity newE = objectMapper.convertValue(request, EmailTemplateEntity.class);
        mapUtils.customMap(newE, entity);
        entity.setEmailTemplateId(id);
        entity = emailTemplateRepository.save(entity);
        addLog("EMAIL_TEMPLATE", "UPDATE", new Gson().toJson(request));
        return objectMapper.convertValue(entity, EmailTemplateResponse.class);
    }

    public List<EmailTemplateResponse> findByType(Integer type) {
        ArrayList<EmailTemplateEntity> list = emailTemplateRepository.findByType(type);

        return list.stream().map(obj ->
                this.objectMapper.convertValue(obj, EmailTemplateResponse.class)
        ).collect(Collectors.toList());
    }
}
