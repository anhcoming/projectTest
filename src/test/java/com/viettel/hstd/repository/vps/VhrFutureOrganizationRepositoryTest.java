package com.viettel.hstd.repository.vps;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VhrFutureOrganizationRepositoryTest {

    @Autowired
    VhrFutureOrganizationRepository organizationRepository;

    @Test
    void checkDepartmentWithUnit() {
        boolean result = organizationRepository.checkDepartmentWithUnit(9014815L, 9019608L);
        Assertions.assertTrue(result);
    }
}