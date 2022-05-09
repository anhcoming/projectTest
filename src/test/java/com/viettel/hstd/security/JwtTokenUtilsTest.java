package com.viettel.hstd.security;

import com.nimbusds.jose.JOSEException;
import com.viettel.hstd.security.sso.SSoResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtTokenUtilsTest {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Test
    void encryptedJsonWebToken() throws InvalidKeySpecException, NoSuchAlgorithmException, ParseException, JOSEException {
        SSoResponse soResponse = new SSoResponse();

        soResponse.setEmployeeCode("142080");
        soResponse.setPhoneNumber("0949810501");
        soResponse.setSysUserId(14696L);

        String token = jwtTokenUtils.generateToken(soResponse);
        SSoResponse sSoResponse = jwtTokenUtils.validateToken(token);

        Assertions.assertEquals(sSoResponse.getEmployeeCode(), soResponse.getEmployeeCode());

    }
}