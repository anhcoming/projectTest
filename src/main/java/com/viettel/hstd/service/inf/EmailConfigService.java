package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.EmailConfigDTO;
import com.viettel.hstd.entity.hstd.EmailConfigEntity;
import org.springframework.mail.javamail.JavaMailSender;

public interface EmailConfigService extends CRUDService<EmailConfigDTO.EmailConfigRequest, EmailConfigDTO.EmailConfigResponse, Long> {
    JavaMailSender configJavaMail(Long emailConfigId);
    EmailConfigEntity getEmailConfig(Long emailConfigId);
}
