package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmailCategoryDTO;
import com.viettel.hstd.entity.hstd.EmailCategoryEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.EmailCategoryRepository;
import com.viettel.hstd.service.inf.EmailCategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class EmailCategoryImp extends BaseService implements EmailCategoryService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EmailCategoryRepository emailCategoryRepository;

    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;

    @Override
    public Page<EmailCategoryDTO.EmailCategoryResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<EmailCategoryEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<EmailCategoryEntity> list;
        if (searchRequest.pagedFlag) {
            list = emailCategoryRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = emailCategoryRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, EmailCategoryDTO.EmailCategoryResponse.class)
        );
    }

    @Override
    public EmailCategoryDTO.EmailCategoryResponse findOneById(Long id) {
        EmailCategoryEntity emailCategoryEntity = emailCategoryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(emailCategoryEntity, EmailCategoryDTO.EmailCategoryResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!emailCategoryRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        emailCategoryRepository.softDelete(id);
        addLog("EMAIL_CATEGORY", "DELETE", id.toString());
        return true;
    }

    @Override
    public EmailCategoryDTO.EmailCategoryResponse create(EmailCategoryDTO.EmailCategoryRequest request) {
        EmailCategoryEntity emailCategoryEntity = objectMapper.convertValue(request, EmailCategoryEntity.class);
        emailCategoryEntity = emailCategoryRepository.save(emailCategoryEntity);
        addLog("EMAIL_CATEGORY", "CREATE", new Gson().toJson(request));
        return objectMapper.convertValue(emailCategoryEntity, EmailCategoryDTO.EmailCategoryResponse.class);
    }


    @Override
    public EmailCategoryDTO.EmailCategoryResponse update(Long id, EmailCategoryDTO.EmailCategoryRequest request) {
        EmailCategoryEntity emailCategoryEntity = emailCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        EmailCategoryEntity newE = objectMapper.convertValue(request, EmailCategoryEntity.class);
        mapUtils.customMap(newE, emailCategoryEntity);
        emailCategoryEntity.setEmailCategoryId(id);
        emailCategoryEntity = emailCategoryRepository.save(emailCategoryEntity);
        addLog("EMAIL_CATEGORY", "UPDATE", new Gson().toJson(request));
        return objectMapper.convertValue(emailCategoryEntity, EmailCategoryDTO.EmailCategoryResponse.class);
    }
}
