package com.viettel.hstd.service.imp;

import com.viettel.hstd.constant.OtpType;
import com.viettel.hstd.dto.hstd.OtpDTO;
import com.viettel.hstd.service.inf.OtpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OtpServiceImpTest {

//    @Autowired
//    private OtpService otpService;
//
//    @Test
//    void create() {
//        OtpDTO.OtpCreateRequest request = new OtpDTO.OtpCreateRequest();
//        request.otpType = OtpType.TERMINATE_CONTRACT;
//        OtpDTO.OtpCreateResponse response = otpService.create(request);
//
//        Assertions.assertNotNull(response);
//    }
}