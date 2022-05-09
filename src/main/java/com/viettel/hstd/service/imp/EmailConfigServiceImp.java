package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.EncryptionData;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmailConfigDTO;
import com.viettel.hstd.entity.hstd.EmailConfigEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.EmailConfigRepository;
import com.viettel.hstd.service.inf.EmailConfigService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class EmailConfigServiceImp extends BaseService implements EmailConfigService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EmailConfigRepository emailConfigRepository;

    @Autowired
    Message message;

    @Autowired
    MapUtils mapUtils;


    @Override
    public Page<EmailConfigDTO.EmailConfigResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<EmailConfigEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<EmailConfigEntity> list;
        if (searchRequest.pagedFlag) {
            list = emailConfigRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = emailConfigRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, EmailConfigDTO.EmailConfigResponse.class)
        );
    }

    //    @PreAuthorize("hasPermission('VIEW EMAIL_CONFIG', '')")
    @Override
    public EmailConfigDTO.EmailConfigResponse findOneById(Long id) {
        EmailConfigEntity emailConfigEntity = emailConfigRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        emailConfigEntity.setPassword(EncryptionData.decrypt(emailConfigEntity.getPassword()));

        return objectMapper.convertValue(emailConfigEntity, EmailConfigDTO.EmailConfigResponse.class);
    }

    //    @PreAuthorize("hasPermission('DELETE EMAIL_CONFIG', '')")
    @Override
    public Boolean delete(Long id) {
        if (!emailConfigRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        emailConfigRepository.softDelete(id);
        addLog("EMAIL_CONFIG", "DELETE", id.toString());
        return true;
    }

    //    @PreAuthorize("hasPermission('CREATE EMAIL_CONFIG', '')")
    @Override
    public EmailConfigDTO.EmailConfigResponse create(EmailConfigDTO.EmailConfigRequest request) {
        EmailConfigEntity emailConfigEntity = objectMapper.convertValue(request, EmailConfigEntity.class);
        emailConfigEntity.setPassword(EncryptionData.encrypt(emailConfigEntity.getPassword()));
        emailConfigEntity = emailConfigRepository.save(emailConfigEntity);
        addLog("EMAIL_CONFIG", "CREATE", new Gson().toJson(request));

        return objectMapper.convertValue(emailConfigEntity, EmailConfigDTO.EmailConfigResponse.class);
    }

    //    @PreAuthorize("hasPermission('UPDATE EMAIL_CONFIG', '')")
    @Override
    public EmailConfigDTO.EmailConfigResponse update(Long id, EmailConfigDTO.EmailConfigRequest request) {
        EmailConfigEntity emailConfigEntity = emailConfigRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        EmailConfigEntity newE = objectMapper.convertValue(request, EmailConfigEntity.class);
        mapUtils.customMap(newE, emailConfigEntity);
        emailConfigEntity.setEmailConfigId(id);
        emailConfigEntity.setPassword(EncryptionData.encrypt(emailConfigEntity.getPassword()));
        emailConfigEntity = emailConfigRepository.save(emailConfigEntity);
        addLog("EMAIL_CONFIG", "UPDATE", new Gson().toJson(request));
        return objectMapper.convertValue(emailConfigEntity, EmailConfigDTO.EmailConfigResponse.class);
    }


    @Override
    public JavaMailSender configJavaMail(Long emailConfigId) {
        EmailConfigEntity emailConfigEntity = getEmailConfig(emailConfigId);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailConfigEntity.getMailServer());
        mailSender.setPort(emailConfigEntity.getPort().intValue());
        mailSender.setUsername(emailConfigEntity.getEmail());
        mailSender.setPassword(EncryptionData.decrypt(emailConfigEntity.getPassword()));
        mailSender.setDefaultEncoding("UTF-8");
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");

        if (emailConfigEntity.getAuthenticate().equals("Không xác thực")) {
            props.put("mail.smtp.auth", "false");
        } else {
            props.put("mail.smtp.auth", "true");
        }

        if (emailConfigEntity.getAuthenticate().equals("SSL")) {
            props.put("mail.smtp.starttls.enable", "false");
            props.put("mail.smtp.ssl.enable", "true");
        } else if (emailConfigEntity.getAuthenticate().equals("TLS")) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        props.put("mail.debug", "true");
        return mailSender;
    }

    @Override
    public EmailConfigEntity getEmailConfig(Long emailConfigId) {
        List<EmailConfigEntity> emailConfigEntities = new ArrayList<>();

        if (emailConfigId == null) {
            emailConfigEntities = emailConfigRepository.findAll();
        } else {
            EmailConfigEntity emailConfigEntity = emailConfigRepository.findById(emailConfigId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy cấu hình Email với ID " + emailConfigId));
            emailConfigEntities.add(emailConfigEntity);
        }

        if (emailConfigEntities == null || emailConfigEntities.isEmpty()) {
            throw new NotFoundException(message.getMessage("message.not_found_email_send"));
        }
        EmailConfigEntity emailConfigEntity = emailConfigEntities.get(0);
        return emailConfigEntity;
    }
}
