package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.core.utils.CustomMapper;
import com.viettel.hstd.core.utils.EmailUtils;
import com.viettel.hstd.entity.hstd.CvEntity;
import com.viettel.hstd.entity.hstd.EmailTemplateEntity;
import com.viettel.hstd.entity.hstd.InterviewSessionCvEntity;
import com.viettel.hstd.repository.hstd.EmailTemplateRepository;
import com.viettel.hstd.repository.hstd.InterviewSessionCvRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class EmailServiceImpTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Autowired
    private InterviewSessionCvRepository interviewSessionCvRepository;

    @Test
    void sendMessage() {
        InterviewSessionCvEntity interviewCv = interviewSessionCvRepository.findById(1451L).orElse(null);
        if(interviewCv == null) {
            System.out.println("Error");
        }

        EmailTemplateEntity templateEntity = emailTemplateRepository.findById(1L).orElse(null);
        if (templateEntity == null) {
            System.out.println("Error");
        }
        CvEntity cvEntity = interviewCv.getCvEntity();
        Map<String, String> map = CustomMapper.convert(cvEntity);

        if (interviewCv != null) {
            map.putAll(CustomMapper.convert(interviewCv));
        }
        String content =  EmailUtils.replaceContent(templateEntity.getContent(), map);
        System.out.println(content);
    }
}