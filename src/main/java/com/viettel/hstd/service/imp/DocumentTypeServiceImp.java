package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.DocumentTypeDTO;
import com.viettel.hstd.entity.hstd.DocumentTypeEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.DocumentTypeRepository;
import com.viettel.hstd.service.inf.DocumentTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentTypeServiceImp extends BaseService implements DocumentTypeService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    DocumentTypeRepository documentTypeRepository;

    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;

    @Override
    public Page<DocumentTypeDTO.DocumentTypeResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<DocumentTypeEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<DocumentTypeEntity> list;
        if (searchRequest.pagedFlag) {
            list = documentTypeRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = documentTypeRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, DocumentTypeDTO.DocumentTypeResponse.class)
        );
    }

    @Override
    public DocumentTypeDTO.DocumentTypeResponse findOneById(Long id) {
        DocumentTypeEntity DocumentTypeEntity = documentTypeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(DocumentTypeEntity, DocumentTypeDTO.DocumentTypeResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!documentTypeRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        documentTypeRepository.softDelete(id);
        addLog("DOCUMENT_TYPE", "DELETE", id.toString());
        return true;
    }

    @Override
    public DocumentTypeDTO.DocumentTypeResponse create(DocumentTypeDTO.DocumentTypeRequest request) {
        DocumentTypeEntity DocumentTypeEntity = objectMapper.convertValue(request, DocumentTypeEntity.class);
        ArrayList<DocumentTypeEntity> lstDocument = documentTypeRepository.findByTypeAndAndCode(request.type, request.code);
        if (lstDocument != null && lstDocument.size() > 0) {
            return null;
        }
        DocumentTypeEntity = documentTypeRepository.save(DocumentTypeEntity);
        addLog("DOCUMENT_TYPE", "CREATE", new Gson().toJson(request));
        return objectMapper.convertValue(DocumentTypeEntity, DocumentTypeDTO.DocumentTypeResponse.class);
    }


    @Override
    public DocumentTypeDTO.DocumentTypeResponse update(Long id, DocumentTypeDTO.DocumentTypeRequest request) {
        DocumentTypeEntity documentTypeEntity = documentTypeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));

        if (!documentTypeEntity.getCode().equals(request.code) && documentTypeRepository.findByTypeAndAndCode(request.type, request.code).size() > 0) {
            throw new BadRequestException("Đã có loại hồ sơ có mã hồ sơ tương tự");
        }

        DocumentTypeEntity newE = objectMapper.convertValue(request, DocumentTypeEntity.class);
        mapUtils.customMap(newE, documentTypeEntity);
        documentTypeEntity.setDocumentTypeId(id);
        documentTypeEntity = documentTypeRepository.save(documentTypeEntity);
        addLog("DOCUMENT_TYPE", "UPDATE", new Gson().toJson(request));
        return objectMapper.convertValue(documentTypeEntity, DocumentTypeDTO.DocumentTypeResponse.class);
    }

    @Override
    public Boolean isExisted(ContractType type, String code, Long id) {
        if (type == null || code == null) {
            return false;
        }
        ArrayList<DocumentTypeEntity> lstDocument = documentTypeRepository.findByTypeAndAndCode(type, code);
        if (lstDocument != null && lstDocument.size() > 0 && id > 0) {
            return lstDocument.stream().anyMatch(x -> x.getDocumentTypeId() != id);
        }
        return lstDocument.size() > 0;
    }
}
