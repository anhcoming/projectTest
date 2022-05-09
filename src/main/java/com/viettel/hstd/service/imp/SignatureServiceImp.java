package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.SignatureDTO.*;
import com.viettel.hstd.entity.hstd.SignatureEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.SignatureRepository;
import com.viettel.hstd.service.inf.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SignatureServiceImp extends BaseService implements SignatureService {

    @Autowired
    SignatureRepository signatureRepository;

    @Autowired
    Message message;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MapUtils mapUtils;

    @Override
    public SignatureResponse findOneById(Long id) {
        SignatureEntity signatureEntity = signatureRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return convertEntity2Response(signatureEntity);
    }

    @Override
    public Boolean delete(Long id) {
        if (!signatureRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        signatureRepository.softDelete(id);
        addLog("EMAIL_CONFIG");
        return true;
    }

    @Override
    public Page<SignatureResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<SignatureEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<SignatureEntity> list;
        if (searchRequest.pagedFlag) {
            list = signatureRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = signatureRepository.findAll(p);
        }

        return list.map(this::convertEntity2Response);
    }

    @Override
    public SignatureResponse create(SignatureRequest request) {
        SignatureEntity signatureEntity = objectMapper.convertValue(request, SignatureEntity.class);
        signatureEntity = signatureRepository.save(signatureEntity);
        addLog("CREATE");
        return convertEntity2Response(signatureEntity);
    }

    @Override
    public SignatureResponse update(Long id, SignatureRequest request) {
        SignatureEntity signatureEntity = signatureRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        SignatureEntity newE = objectMapper.convertValue(request, SignatureEntity.class);
        mapUtils.customMap(newE, signatureEntity);
        signatureEntity.setSignatureId(id);
        signatureEntity = signatureRepository.save(signatureEntity);
        addLog("UPDATE");
        return convertEntity2Response(signatureEntity);
    }

    private SignatureResponse convertEntity2Response(SignatureEntity entity) {
        return objectMapper.convertValue(entity, SignatureResponse.class);
    }
}
