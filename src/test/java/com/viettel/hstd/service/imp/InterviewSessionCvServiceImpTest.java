package com.viettel.hstd.service.imp;

import com.viettel.hstd.service.inf.InterviewSessionCvService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class InterviewSessionCvServiceImpTest {
    @Autowired
    InterviewSessionCvService interviewSessionCvService;
    @Test
    void getEmailContent() {
        String email = interviewSessionCvService.getEmailContent(1451L, 1L);
        System.out.println(email);
    }
}