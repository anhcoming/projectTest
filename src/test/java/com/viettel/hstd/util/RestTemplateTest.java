package com.viettel.hstd.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.core.dto.AccountLoginDTO;
import com.viettel.hstd.core.dto.BaseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RestTemplateTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    Gson gson;

//    @Test
//    @EnabledIf(expression = "#{environment.acceptsProfiles('dev')}", loadContext = true)
//    public void givenDataIsJson_whenDataIsPostedByPostForObject_thenResponseBodyIsNotNull()
//            throws IOException {
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        AccountLoginDTO accountLoginDTO = new AccountLoginDTO();
//        accountLoginDTO.userName = "142080";
//        accountLoginDTO.password = "123456";
//
//        String loginUrl = "http://171.244.9.242:8864/hstd-service/auth/app-login";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        try {
//            String jsonRequest = objectMapper.writeValueAsString(accountLoginDTO);
//            HttpEntity<String> entity = new HttpEntity<String>(jsonRequest, headers);
//
//            String stringResponse = restTemplate.postForObject(
//                    loginUrl,
//                    entity,
//                    String.class);
//
//            BaseResponse<String> response = gson.fromJson(stringResponse, BaseResponse.class);
////            if (response.lstSolarActiveAndDownTime == null) response.lstSolarActiveAndDownTime = new ArrayList<>();
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
////        assertNotNull(personResultAsJsonStr);
//    }


}
