package com.viettel.hstd.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.viettel.voffice.ws_autosign.service.KttsVofficeCommInpuParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

class JacksonTest {

    @Test
    public void convertObjectToJson() throws JsonProcessingException {
        KttsVofficeCommInpuParam kttsVofficeCommInpuParam = new KttsVofficeCommInpuParam();

        kttsVofficeCommInpuParam.setHinhthucVanban(479L);
        kttsVofficeCommInpuParam.setSender("123456");

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(kttsVofficeCommInpuParam);

        Assertions.assertNotNull(json);
    }
}