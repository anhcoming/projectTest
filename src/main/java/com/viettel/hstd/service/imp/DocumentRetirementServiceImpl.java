package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.DocumentRetirementDTO.*;
import com.viettel.hstd.entity.hstd.DocumentRetirementEntity;
import com.viettel.hstd.entity.hstd.DocumentTypeEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.DocumentRetirementRepository;
import com.viettel.hstd.service.inf.DocumentRetirementService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class DocumentRetirementServiceImpl extends BaseService implements DocumentRetirementService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    DocumentRetirementRepository documentRetirementRepository;

    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;

    @Override
    public Page<DocumentRetirementResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<DocumentRetirementEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<DocumentRetirementEntity> list;
        if (searchRequest.pagedFlag) {
            list = documentRetirementRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = documentRetirementRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, DocumentRetirementResponse.class)
        );
    }

    @Override
    public DocumentRetirementResponse findOneById(Long id) {
        DocumentRetirementEntity document = documentRetirementRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(document, DocumentRetirementResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!documentRetirementRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        documentRetirementRepository.softDelete(id);
        addLog("DOCUMENT_RETIREMENT", "DELETE", id.toString());
        return true;
    }

    @Override
    public DocumentRetirementResponse create(DocumentRetirementRequest request) {
        DocumentRetirementEntity document = objectMapper.convertValue(request, DocumentRetirementEntity.class);
        ArrayList<DocumentRetirementEntity> lstDocument = documentRetirementRepository.findByCode(request.code);
        if (lstDocument != null && lstDocument.size() > 0) {
            return null;
        }
        document = documentRetirementRepository.save(document);
        addLog("DOCUMENT_RETIREMENT", "CREATE", new Gson().toJson(request));
        return objectMapper.convertValue(document, DocumentRetirementResponse.class);
    }


    @Override
    public DocumentRetirementResponse update(Long id, DocumentRetirementRequest request) {
        DocumentRetirementEntity document = documentRetirementRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        DocumentRetirementEntity newE = objectMapper.convertValue(request, DocumentRetirementEntity.class);
        mapUtils.customMap(newE, document);
        document.setDocumentRetirementId(id);
        ArrayList<DocumentRetirementEntity> lstDocument = documentRetirementRepository.findByCode(request.code);
        if (lstDocument != null && lstDocument.size() > 0) {
            if (lstDocument.stream().anyMatch(x -> x.getDocumentRetirementId() == id)) {
                return null;
            }
        }
        document = documentRetirementRepository.save(document);
        addLog("DOCUMENT_RETIREMENT", "UPDATE", new Gson().toJson(request));
        return objectMapper.convertValue(document, DocumentRetirementResponse.class);
    }

    @Override
    public Boolean isExisted(String code, Long id) {
        if (code == null) {
            return false;
        }
        ArrayList<DocumentRetirementEntity> lstDocument = documentRetirementRepository.findByCode(code);
        if (lstDocument != null && lstDocument.size() > 0 && id > 0) {
            return lstDocument.stream().anyMatch(x -> x.getDocumentRetirementId() != id);
        }
        return lstDocument.size() > 0;
    }
}

