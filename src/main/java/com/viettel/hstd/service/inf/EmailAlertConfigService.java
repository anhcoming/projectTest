package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.EmailAlertConfigDTO;
import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmailAlertConfigService {
    void save(EmailAlertConfigDTO.Request request);

    void update(Long id, EmailAlertConfigDTO.Request request);

    Page<EmailAlertConfigDTO.Response> search(EmailAlertConfigDTO.SearchCriteria searchCriteria);

    EmailAlertConfigDTO.SingleResponse findById(Long id);

    void deleteById(Long id);

    String createEmailContent(String template, List<RecruitmentProgressDTO.EmailResponse> content, String name, String date);
}
