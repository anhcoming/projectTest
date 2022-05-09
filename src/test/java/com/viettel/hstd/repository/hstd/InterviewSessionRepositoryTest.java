package com.viettel.hstd.repository.hstd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class InterviewSessionRepositoryTest {
    @Autowired
    InterviewSessionRepository interviewSessionRepository;
    @Autowired
    InterviewSessionCvRepository interviewSessionCvRepository;
    @Autowired
    Environment environment;

    @Test
    void isHasContract() {
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            boolean result = interviewSessionRepository.isHasContract(851L);
            System.out.println(result);
        }
    }
}