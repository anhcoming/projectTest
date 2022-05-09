package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.core.constant.ConstantConfig;
import com.viettel.hstd.core.utils.AuthenticateUtils;
import com.viettel.hstd.core.utils.CustomMapper;
import com.viettel.hstd.core.utils.EmailUtils;
import com.viettel.hstd.core.utils.StringUtils;
import com.viettel.hstd.dto.hstd.EmailDTO;
import com.viettel.hstd.dto.hstd.EmployeeVhrTempDTO.*;
import com.viettel.hstd.dto.hstd.MultipleEmailDTO;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.EmailConfigService;
import com.viettel.hstd.service.inf.EmailService;
import com.viettel.hstd.service.inf.EmployeeVhrTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Service
public class EmailServiceImp implements EmailService {
    @Autowired
    EmailConfigService emailConfigService;

    @Autowired
    private EmailCategoryRepository emailCategoryRepository;

    @Autowired
    private RecruiteeAccountRepository recruiteeAccountRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

//    @Autowired
//    private DocumentTypeRepository documentTypeRepository;
    @Autowired
    private InterviewSessionCvRepository interviewSessionCvRepository;
//    @Autowired
//    private RecruitProfileAttachmentRepository recruitProfileAttachmentRepository;
    @Value("${app.store.path-store-media}")
    String pathStore;

    @Autowired
    private EmployeeVhrTempService employeeVhrTempService;
    @Autowired
    AuthenticationFacade authenticationFacade;

    @Override
    public void sendMessage(String subject, Multipart multipart, String email) {
        JavaMailSender javaMailSender = emailConfigService.configJavaMail(null);
        if (emailConfigService.getEmailConfig(null).getIsActive()) {
            Properties props = System.getProperties();
            props.setProperty("mail.debug", "false");
            Session session = Session.getDefaultInstance(props);
            MimeMessage mimeMessage = new MimeMessage(session);
            try {
                mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
                InternetAddress sender = new InternetAddress(emailConfigService.getEmailConfig(null).getEmail());
                mimeMessage.setFrom(sender);
                InternetAddress[] toList = InternetAddress.parse(email);
                mimeMessage.setRecipients(Message.RecipientType.TO, toList);
                mimeMessage.setSubject(subject);
                mimeMessage.setContent(multipart);
                javaMailSender.send(mimeMessage);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public Boolean sendMessage(EmailDTO model) {
        JavaMailSender javaMailSender = emailConfigService.configJavaMail(model.emailConfigId);
        InterviewSessionCvEntity interviewCv = interviewSessionCvRepository.findById(model.interviewSessionCvId).get();
        if (interviewCv == null && interviewCv.getCvEntity() != null) {
            return false;
        }
        interviewCv.setResultEmailSendDate(LocalDate.now());
        interviewSessionCvRepository.save(interviewCv);
        CvEntity cvEntity = interviewCv.getCvEntity();
        if (model.emailSend == null || model.emailSend.trim().length() == 0) {
            model.emailSend = cvEntity.getEmail();
        }
        //<editor-fold desc="Tao tai khoan dang nhap">
        RecruiteeAccountEntity accountEntity = new RecruiteeAccountEntity();
        String password = addOrUpdateAccount(cvEntity, accountEntity, interviewCv);
        //</editor-fold>
        EmailTemplateEntity templateEntity = emailTemplateRepository.findById(model.emailTemplateId).orElse(null);
        if (templateEntity == null) {
            return false;
        }
        if (emailConfigService.getEmailConfig(model.emailConfigId).getIsActive()) {
            Properties props = System.getProperties();
            props.setProperty("mail.debug", "false");
            Session session = Session.getDefaultInstance(props);
            MimeMessage mimeMessage = new MimeMessage(session);
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            try {
                //<editor-fold desc="Dinh kem thong tin dang nhap cua nguoi dung">
                Map<String, String> map = CustomMapper.convert(cvEntity);
                map.put("Username", accountEntity.getLoginName());
                map.put("Password", password);
                if (interviewCv != null) {
                    map.putAll(CustomMapper.convert(interviewCv));
                }

                model.content = (model.content == null
                        || model.content.trim().length() == 0) ? templateEntity.getContent() : model.content;
                model.content = EmailUtils.replaceContent(model.content, map);
                //</editor-fold>
                messageBodyPart.setContent(model.content, "text/html; charset=utf-8");
                multipart.addBodyPart(messageBodyPart);
                mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
                InternetAddress sender = new InternetAddress(emailConfigService.getEmailConfig(model.emailConfigId).getEmail());
                mimeMessage.setFrom(sender);
                InternetAddress[] toList = InternetAddress.parse(model.emailSend);
                mimeMessage.setRecipients(Message.RecipientType.TO, toList);
                //<editor-fold desc="Them file dinh kem vao email">
                if (model.filePath != null && model.filePath.size() > 0) {
                    for (String item : model.filePath) {
                        try {
                            Path fileStorageLocation = Paths.get(pathStore, item.replace("-", "/"))
                                    .toAbsolutePath().normalize();
                            Resource resource = new UrlResource(fileStorageLocation.toUri());
                            MimeBodyPart attachment = new MimeBodyPart();
                            attachment.setFileName(resource.getFilename());
                            attachment.attachFile(resource.getFile());
                            multipart.addBodyPart(attachment);
                        } catch (Exception e) {
                        }
                    }
                }
                //</editor-fold>

                mimeMessage.setSubject(model.name);
                mimeMessage.setContent(multipart);
                javaMailSender.send(mimeMessage);
                //<editor-fold desc="Them bang lich su gui email">
                EmailCategoryEntity entity = new EmailCategoryEntity();
                SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
                entity.setCreatedName(sSoResponse.getFullName());
                entity.setContent(model.content);
                entity.setDelFlag(false);
                entity.setIsActive(true);
                entity.setEmailSend(model.emailSend);
                entity.setIsStatus(true);
                entity.setEmailConfigId(model.emailConfigId);
                entity.setName(model.name);
                entity.setIsStatus(true);
                entity.setEmailTemplateId(model.emailTemplateId);

                emailCategoryRepository.save(entity);
                //</editor-fold>
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    @Override
    public Boolean sendOffer(MultipleEmailDTO multipleEmailDTO) {
        JavaMailSender javaMailSender = emailConfigService.configJavaMail(null);
        ArrayList<InterviewSessionCvEntity> lstInterviewCvs = interviewSessionCvRepository.findByIdIn(multipleEmailDTO.interviewSessionCvId);
//        ArrayList<CvEntity> lstCvs = cvRepository.findByIdIn(multipleEmailDTO.interviewSessionCvId);
        if (lstInterviewCvs != null && lstInterviewCvs.size() > 0) {
            EmailTemplateEntity templateEntity = emailTemplateRepository.findById(multipleEmailDTO.emailTemplateId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy template gửi email"));
            if (templateEntity == null) {
                return false;
            }
            //<editor-fold desc="khoi tao file dinh kem">
            List<MimeBodyPart> attachments = new ArrayList<>();
            if (multipleEmailDTO.filePath != null && multipleEmailDTO.filePath.size() > 0) {
                for (String item : multipleEmailDTO.filePath) {
                    try {
                        Path fileStorageLocation = Paths.get(pathStore, item.replace("-", "/"))
                                .toAbsolutePath().normalize();
                        Resource resource = new UrlResource(fileStorageLocation.toUri());
                        MimeBodyPart attachment = new MimeBodyPart();
                        attachment.setFileName(resource.getFilename());
                        attachment.attachFile(resource.getFile());
                        attachments.add(attachment);
                    } catch (Exception e) {
                    }
                }
            }
            //</editor-fold>
            SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();

            for (InterviewSessionCvEntity interviewCv : lstInterviewCvs) {
                CvEntity cvEntity = interviewCv.getCvEntity();
                String content = (multipleEmailDTO.content == null
                        || multipleEmailDTO.content.trim().length() == 0) ? templateEntity.getContent() : multipleEmailDTO.content;
                //<editor-fold desc="Tao tai khoan dang nhap">
                RecruiteeAccountEntity accountEntity = new RecruiteeAccountEntity();
                String password = addOrUpdateAccount(cvEntity, accountEntity, interviewCv);
                //</editor-fold>
//                if (interviewCv.getContractType() != null && interviewCv.getContractType() > 0) {
//                    addDocumentAttachment(interviewCv.getContractType(), interviewCv.getInterviewSessionCvId(), accountEntity.getRecruiteeAccountId());
//                }
                if (emailConfigService.getEmailConfig(null).getIsActive()) {
                    Properties props = System.getProperties();
                    props.setProperty("mail.debug", "false");
                    Session session = Session.getDefaultInstance(props);
                    MimeMessage mimeMessage = new MimeMessage(session);
                    Multipart multipart = new MimeMultipart();
                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    try {
                        //<editor-fold desc="Dinh kem thong tin dang nhap cua nguoi dung">
//                        Map<String, Object> map = objectMapper.convertValue(cvEntity, Map.class);
                        Map<String, String> map = CustomMapper.convert(cvEntity);
                        map.put("Username", accountEntity.getLoginName());
                        map.put("Password", password);
                        content = EmailUtils.replaceContent(content, map);
                        //</editor-fold>
                        messageBodyPart.setContent(content, "text/html; charset=utf-8");
                        multipart.addBodyPart(messageBodyPart);
                        mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
                        InternetAddress sender = new InternetAddress(emailConfigService.getEmailConfig(null).getEmail());
                        mimeMessage.setFrom(sender);
                        InternetAddress[] toList = InternetAddress.parse(cvEntity.getEmail());
                        mimeMessage.setRecipients(Message.RecipientType.TO, toList);

                        //<editor-fold desc="Them file dinh kem vao email">
                        if (attachments.size() > 0) {
                            for (MimeBodyPart attachment : attachments) {
                                multipart.addBodyPart(attachment);
                            }
                        }
                        //</editor-fold>
                        mimeMessage.setSubject(templateEntity.getName());
                        mimeMessage.setContent(multipart);
                        javaMailSender.send(mimeMessage);
                        //<editor-fold desc="Them bang lich su gui email">
                        EmailCategoryEntity entity = new EmailCategoryEntity();
                        entity.setCreatedName(sSoResponse.getFullName());
                        entity.setContent(content);

                        entity.setEmailSend(cvEntity.getEmail());
                        entity.setIsStatus(true);
                        entity.setEmailConfigId(multipleEmailDTO.emailConfigId);
                        entity.setName(templateEntity.getName());
                        entity.setIsStatus(true);
                        entity.setEmailTemplateId(multipleEmailDTO.emailTemplateId);
                        emailCategoryRepository.save(entity);
                        //</editor-fold>
                        return true;
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
                interviewCv.setResultEmailSendDate(LocalDate.now());
                interviewSessionCvRepository.save(interviewCv);
            }

        }
        return false;
    }

    @Override
    public Boolean sendNotify(EmailDTO model) {
        JavaMailSender javaMailSender = emailConfigService.configJavaMail(null);

        EmployeeVhrTempResponse employee = employeeVhrTempService.findOneById(model.employeeId);
        if (employee == null) {
            return false;
        }
        EmailTemplateEntity templateEntity = emailTemplateRepository.findById(model.emailTemplateId).get();
        if (templateEntity == null) {
            return false;
        }
        if (emailConfigService.getEmailConfig(null).getIsActive()) {
            Properties props = System.getProperties();
            props.setProperty("mail.debug", "false");
            Session session = Session.getDefaultInstance(props);
            MimeMessage mimeMessage = new MimeMessage(session);
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            try {
//                Map<String, Object> map = objectMapper.convertValue(employee, Map.class);
                Map<String, String> map = CustomMapper.convert(employee);

                model.content = (model.content == null
                        || model.content.trim().length() == 0) ? templateEntity.getContent() : model.content;
                model.content = EmailUtils.replaceContent(model.content, map);
                messageBodyPart.setContent(model.content, "text/html; charset=utf-8");
                multipart.addBodyPart(messageBodyPart);
                mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
                InternetAddress sender = new InternetAddress(emailConfigService.getEmailConfig(null).getEmail());
                mimeMessage.setFrom(sender);
                InternetAddress[] toList = InternetAddress.parse(model.emailSend);
                mimeMessage.setRecipients(Message.RecipientType.TO, toList);

                //<editor-fold desc="Them file dinh kem vao email">
                if (model.filePath != null && model.filePath.size() > 0) {
                    for (String item : model.filePath) {
                        try {
                            Path fileStorageLocation = Paths.get(pathStore, item.replace("-", "/"))
                                    .toAbsolutePath().normalize();
                            Resource resource = new UrlResource(fileStorageLocation.toUri());
                            MimeBodyPart attachment = new MimeBodyPart();
                            attachment.setFileName(resource.getFilename());
                            attachment.attachFile(resource.getFile());
                            multipart.addBodyPart(attachment);
                        } catch (Exception e) {
                        }
                    }
                }
                //</editor-fold>

                mimeMessage.setSubject(model.name);
                mimeMessage.setContent(multipart);
                javaMailSender.send(mimeMessage);
                //<editor-fold desc="Them bang lich su gui email">
                SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
                EmailCategoryEntity entity = new EmailCategoryEntity();
                entity.setContent(model.content);
                entity.setDelFlag(false);
                entity.setIsActive(true);
                entity.setEmailSend(model.emailSend);
                entity.setIsStatus(true);
                entity.setEmailConfigId(model.emailConfigId);
                entity.setName(model.name);
                entity.setIsStatus(true);
                entity.setCreatedName(sSoResponse.getFullName());
                entity.setEmailTemplateId(model.emailTemplateId);
                emailCategoryRepository.save(entity);
                //</editor-fold>
                EmployeeVhrTempRequest employeeRequest = objectMapper.convertValue(employee, EmployeeVhrTempRequest.class);
                employeeRequest.notifySendDate = LocalDate.now();
                employeeVhrTempService.update(employee.employeeVhrTempId, employeeRequest);
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //    /**
//     * Them thong tin loai tai lieu ung vien can upload
//     *
//     * @param contractType Loai hop dong de xuat
//     * @param cvId         ma ung vien
//     * @param userId       ma nguoi dung
//     */
//    private void addDocumentAttachment(Integer contractType, Long cvId, Long userId) {
//        if (contractType != null && contractType > 0) {
//            ArrayList<DocumentTypeEntity> lstDocument = documentTypeRepository.findByType(contractType);
//            for (DocumentTypeEntity item : lstDocument) {
//                RecruitProfileAttachmentEntity entity = new RecruitProfileAttachmentEntity();
//                entity.setAccountId(userId);
//                entity.setInterviewSessionCvId(cvId);
//                entity.setDocumentTypeId(item.getDocumentTypeId());
//                entity.setStatus(ProfileStatusConstant.pending);
//                recruitProfileAttachmentRepository.save(entity);
//            }
//        }
//    }
    private String addOrUpdateAccount(CvEntity cvEntity, RecruiteeAccountEntity accountEntity, InterviewSessionCvEntity interviewCv) {
        String account = "";
        if (interviewCv.getRecruiteeAccountEntities() != null && interviewCv.getRecruiteeAccountEntities().size() > 0) {
            RecruiteeAccountEntity recruiteeAccountEntity = interviewCv.getRecruiteeAccountEntities().get(0);
            accountEntity.setRecruiteeAccountId(recruiteeAccountEntity.getRecruiteeAccountId());
            accountEntity.setUpdatedBy(recruiteeAccountEntity.getUpdatedBy());
            accountEntity.setUpdatedAt(recruiteeAccountEntity.getUpdatedAt());
            accountEntity.setInterviewSessionCvEntity(recruiteeAccountEntity.getInterviewSessionCvEntity());
            accountEntity.setCreatedAt(recruiteeAccountEntity.getCreatedAt());
            accountEntity.setIsActive(recruiteeAccountEntity.getIsActive());
            accountEntity.setCreatedBy(recruiteeAccountEntity.getCreatedBy());
            accountEntity.setDelFlag(recruiteeAccountEntity.getDelFlag());
            accountEntity.setLoginName(recruiteeAccountEntity.getLoginName());
            account = recruiteeAccountEntity.getLoginName();
        }
        if (accountEntity.getRecruiteeAccountId() == 0) {
            account = StringUtils.generateAccountName(cvEntity.getFullName(), ConstantConfig.defaultAccountPrefix);
            List<RecruiteeAccountEntity> lstAccount = recruiteeAccountRepository.findByLoginNameContainingIgnoreCase(account);
            if (lstAccount != null && lstAccount.size() > 0) {
                account += lstAccount.size();
            }
        }
//        String password = StringUtils.generateRandomPassword(8);
        String password = "12345678";
        accountEntity.setInterviewSessionCvEntity(interviewCv);
        accountEntity.setLoginName(account);
        accountEntity.setPassword(AuthenticateUtils.encryptText(password));
        accountEntity = recruiteeAccountRepository.save(accountEntity);
        return password;
    }
}
