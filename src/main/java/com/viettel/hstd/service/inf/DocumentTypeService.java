package com.viettel.hstd.service.inf;

import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.DocumentTypeDTO;

public interface DocumentTypeService extends CRUDService<DocumentTypeDTO.DocumentTypeRequest, DocumentTypeDTO.DocumentTypeResponse, Long> {
    Boolean isExisted(ContractType type, String code, Long id);
}
