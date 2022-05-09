package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.AccountLoginDTO;
import com.viettel.hstd.core.dto.BaseResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    private AuthController authController;

//    @Test
//    void appLogin() {
//        AccountLoginDTO accountLoginDTO = new AccountLoginDTO();
//        accountLoginDTO.userName = "142080";
//        accountLoginDTO.password = "123456";
//        BaseResponse<String> baseResponse = authController.appLogin(accountLoginDTO);
//
//        assertEquals(baseResponse.status, 1);
//
//
//    }
}