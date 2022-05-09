package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.DocumentRetirementDTO.*;

public interface DocumentRetirementService extends CRUDService<DocumentRetirementRequest, DocumentRetirementResponse, Long> {
    Boolean isExisted(String code, Long id);
}
