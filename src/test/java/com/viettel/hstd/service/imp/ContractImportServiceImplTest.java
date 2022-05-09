package com.viettel.hstd.service.imp;

import com.viettel.hstd.core.utils.StringUtils;
import com.viettel.hstd.service.ContractImportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Transactional
class ContractImportServiceImplTest {
    @Autowired
    Environment environment;

    @Autowired
    ContractImportService contractImportService;

    @Test
    void importContractFromExcel() {
//        if (Arrays.asList(environment.getActiveProfiles()).contains("real-local")) {
//
//        }
        String abc = "142080.5";
        if (StringUtils.isDouble(abc)) {
            abc = (int) Double.parseDouble(abc) + "";
        }
        System.out.println(abc);
    }

    @Test
    void deleteContractImport() {
        if (Arrays.asList(environment.getActiveProfiles()).contains("real-local")) {
        }
    }
}