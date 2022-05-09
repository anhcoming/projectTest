package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class InterviewSessionCvControllerTest {
    @Autowired
    InterviewSessionCvController controller;
    @Test
    void getEmailContent() {
        BaseResponse<String> response = controller.getEmailContent(757L,16L);
        System.out.println(response.toString());
    }
}