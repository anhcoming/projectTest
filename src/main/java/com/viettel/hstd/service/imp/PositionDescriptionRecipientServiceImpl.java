package com.viettel.hstd.service.imp;

import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.dto.hstd.PositionDescriptionRecipientDTO;
import com.viettel.hstd.entity.hstd.EmailCategoryEntity;
import com.viettel.hstd.entity.hstd.EmailTemplateEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.repository.hstd.EmailCategoryRepository;
import com.viettel.hstd.repository.hstd.EmailTemplateRepository;
import com.viettel.hstd.repository.hstd.PositionDescriptionRecipientRepository;
import com.viettel.hstd.service.inf.EmailAlertConfigService;
import com.viettel.hstd.service.inf.EmailConfigService;
import com.viettel.hstd.service.inf.PositionDescriptionRecipientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class PositionDescriptionRecipientServiceImpl implements PositionDescriptionRecipientService {
    private final PositionDescriptionRecipientRepository positionDescriptionRecipientRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailCategoryRepository emailCategoryRepository;
    private final Message message;
    private final EmailConfigService emailConfigService;
    @Lazy
    private final PositionDescriptionRecipientService self;

    private static final Long EMAIL_TEMPLATE_ID = 951L;
    private static final Long SENDER_VT_ID = 301L;

    @Transactional(readOnly = true)
    @Override
    public List<PositionDescriptionRecipientDTO.Response> getRecipientsByPositionDescriptionId(Long positionDescriptionId) {
        return positionDescriptionRecipientRepository.getRecipientsByPositionDescriptionId(positionDescriptionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Pair<String, String>, List<PositionDescriptionRecipientDTO.EmailResponse>> getEmailAlertPerEmployee() {
        return positionDescriptionRecipientRepository.findAllStream()
                .collect(Collectors.groupingBy(emailResponse -> Pair.of(emailResponse.getEmployeeName(), emailResponse.getEmployeeEmail())));
    }


    @Async("taskScheduler")
    @Scheduled(cron = "0 0 8 * * MON")
    @Transactional
    public void sendEmailAlert() throws MessagingException {
        EmailTemplateEntity emailTemplateEntity = emailTemplateRepository.findById(EMAIL_TEMPLATE_ID)
                .orElseThrow(() -> new BadRequestException(message.getMessage("message.not_found")));
        String template = emailTemplateEntity.getContent();
        Map<Pair<String, String>, List<PositionDescriptionRecipientDTO.EmailResponse>> map = self.getEmailAlertPerEmployee();
        self.sendMail(template, map);

    }

    @Transactional
    public void sendMail(String template, Map<Pair<String, String>, List<PositionDescriptionRecipientDTO.EmailResponse>> map) throws MessagingException {
        if (!map.isEmpty()) {
            List<EmailCategoryEntity> emailCategoryEntities = new ArrayList<>();
            JavaMailSender javaMailSender = emailConfigService.configJavaMail(SENDER_VT_ID);
            Set<Pair<String, String>> keySet = map.keySet();
            for (Pair<String, String> info : keySet) {
                if (Objects.nonNull(info.getRight())) {
                    emailCategoryEntities.add(sendEmailPerson(template, map, javaMailSender, info));
                }
            }
            emailCategoryRepository.saveAll(emailCategoryEntities);
        }
    }

    private EmailCategoryEntity sendEmailPerson(String template, Map<Pair<String, String>, List<PositionDescriptionRecipientDTO.EmailResponse>> map, JavaMailSender javaMailSender, Pair<String, String> info) throws MessagingException {
        MimeMessage emailMessage = javaMailSender.createMimeMessage();
        String content = createEmailContent(template, map.get(info), info.getLeft());
        emailMessage.setHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
        InternetAddress[] toList = InternetAddress.parse(info.getRight());
        emailMessage.setRecipients(javax.mail.Message.RecipientType.TO, toList);
        emailMessage.setFrom("tcld_ctct@viettel.com.vn");
        emailMessage.setSubject(this.message.getMessage("email.alert.position-description.subject"));
        Multipart multipart = new MimeMultipart();
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(content, "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);
        emailMessage.setContent(multipart);
        javaMailSender.send(emailMessage);

        EmailCategoryEntity emailCategoryEntity = new EmailCategoryEntity();
        emailCategoryEntity.setEmailTemplateId(EMAIL_TEMPLATE_ID);
        emailCategoryEntity.setEmailSend(info.getRight());
        emailCategoryEntity.setEmailConfigId(SENDER_VT_ID);
        emailCategoryEntity.setName(this.message.getMessage("email.alert.position-description.subject"));
        emailCategoryEntity.setCreatedName("AUTO");
        emailCategoryEntity.setContent(content);
        return emailCategoryEntity;

    }

    private String createEmailContent(String template, List<PositionDescriptionRecipientDTO.EmailResponse> contents, String name) {
        Map<String, String> valueMap = new ConcurrentHashMap<>();
        valueMap.put("name", StringUtils.defaultIfEmpty(name, ""));
        valueMap.put("table", createTable(contents));
        valueMap.put("count", String.valueOf(contents.size()));
        StrSubstitutor strSubstitutor = new StrSubstitutor(valueMap);
        return strSubstitutor.replace(template);
    }

    private String createTable(List<PositionDescriptionRecipientDTO.EmailResponse> contents) {
        StringBuilder result = new StringBuilder("<table class=\"tftable\" border=\"1\" cellpadding=\"10\"><tr><th>STT</th><th>Đơn vị</th><th>Phòng ban</th><th>Tổ nhóm</th><th>Chức danh</th><th>Nhân sự phụ trách</th></tr>");
        int numberOrder = 1;
        for (PositionDescriptionRecipientDTO.EmailResponse row : contents) {
            result.append(MessageFormat.format("<tr>\n" +
                            "<td style=\"text-align: center;\">{0}</td>\n" +
                            "<td style=\"text-align: center;\">{1}</td>\n" +
                            "<td style=\"text-align: center;\">{2}</td>\n" +
                            "<td style=\"text-align: center;\">{3}</td>\n" +
                            "<td style=\"text-align: center;\">{4}</td>\n" +
                            "<td style=\"text-align: center;\">{5}</td>\n" +
                            "</tr>", numberOrder++, row.getUnitName(),
                    StringUtils.defaultIfEmpty(row.getDepartmentName(), StringUtils.EMPTY),
                    StringUtils.defaultIfEmpty(row.getGroupName(), StringUtils.EMPTY),
                    row.getPositionName(), row.getEmployeeName()));
        }
        result.append("</table>");
        return result.toString();

    }
}
