package com.viettel.hstd.service.imp;

import com.google.gson.Gson;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.dto.hstd.SysLogDTO;
import com.viettel.hstd.repository.hstd.SysLogRepository;
import com.viettel.hstd.service.inf.SysLogService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SysLogServiceImpTest {

    @Autowired
    SysLogService sysLogService;

    @Autowired
    SysLogRepository sysLogRepository;

    @Autowired
    Gson gson;

    @BeforeEach
    void setUp() {
    }

//    @Test
//    @DisplayName("Check search date specification")
//    void checkSearchDateSpecification() {
//        String searchJson = "{\n" +
//                "  \"page\": 0,\n" +
//                "  \"size\": 10,\n" +
//                "  \"pagedFlag\": true,\n" +
//                "  \"sortList\": [\n" +
//                "\n" +
//                "  ],\n" +
//                "  \"criteriaList\": [\n" +
//                "    {\n" +
//                "      \"field\": \"createdAt\",\n" +
//                "      \"operation\": \">=\",\n" +
//                "      \"value\": \"01/06/2021 01:01:01\",\n" +
//                "      \"type\": 2,\n" +
//                "      \"andFlag\": true\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";
//
//        Page<SysLogDTO.SysLogResponse> response = sysLogService.findPage(gson.fromJson(searchJson, SearchDTO.class));
//        LocalDateTime createdAt = LocalDateTime.of(LocalDate.of(2021,6,1), LocalTime.of(1,1,1));
//        if (sysLogRepository.existsByCreatedAtGreaterThanEqual(createdAt)) {
//            Assertions.assertTrue(response.getContent().size() > 0);
//        } else {
//            assertNotNull(response);
//        }
//
//    }

    @AfterEach
    void tearDown() {
    }
}