package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.EmailTemplateDTO.*;

import java.util.ArrayList;
import java.util.List;

public interface EmailTemplateService extends CRUDService<EmailTemplateRequest, EmailTemplateResponse, Long> {
    List<EmailTemplateResponse> findByType(Integer type);
}
