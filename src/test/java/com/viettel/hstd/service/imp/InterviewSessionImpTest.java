package com.viettel.hstd.service.imp;

import com.google.gson.Gson;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.dto.hstd.InterviewSessionDTO;
import com.viettel.hstd.entity.hstd.InterviewSessionEntity;
import com.viettel.hstd.repository.hstd.InterviewSessionRepository;
import com.viettel.hstd.service.inf.InterviewSessionService;
import liquibase.pro.packaged.A;
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
class InterviewSessionImpTest {

    @Autowired
    InterviewSessionService interviewSessionService;

    @Autowired
    InterviewSessionRepository interviewSessionRepository;

    @Autowired
    Gson gson;

//    @BeforeEach
//    void setUp() {
//    }
//
//    @Test
//    @DisplayName("Check Search LocalDate Specification")
//    void checkSearchLocalDateSpecification() {
//        String jsonSearch = "{\n" +
//                "  \"page\": 0,\n" +
//                "  \"size\": 10,\n" +
//                "  \"pagedFlag\": true,\n" +
//                "  \"sortList\": [\n" +
//                "  ],\n" +
//                "  \"criteriaList\": [\n" +
//                "    {\n" +
//                "      \"field\": \"startDate\",\n" +
//                "      \"operation\": \">\",\n" +
//                "      \"value\": \"01/06/2021 01:01:01\",\n" +
//                "      \"type\": 2,\n" +
//                "      \"andFlag\": true\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";
//
//        Page<InterviewSessionDTO.InterviewSessionResponse> interviewSessionEntityPage = interviewSessionService.findPage(gson.fromJson(jsonSearch, SearchDTO.class));
//        LocalDateTime startDate = LocalDateTime.of(LocalDate.of(2021, 6, 1), LocalTime.of(1,1,1));
//        if (interviewSessionRepository.existsByStartDateGreaterThanEqual(startDate)) {
//            Assertions.assertTrue(interviewSessionEntityPage.getContent().size() > 0);
//        } else {
//            assertNotNull(interviewSessionEntityPage);
//        }
//
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
}