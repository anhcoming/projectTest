package com.viettel.hstd.service.imp;

import com.viettel.hstd.service.inf.InsuranceTerminateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class InsuranceTerminateTest {

    @Autowired
    private InsuranceTerminateService insuranceTerminateService;

    @Test
    void addContractToInsuranceSession() {
        Set<Long> terminateIds = new HashSet<>();
        terminateIds.add(802L);
        insuranceTerminateService.addContractToInsuranceSession(terminateIds, 1L);
    }

    @Test
    void removeContractToInsuranceSession() {
        Set<Long> terminateIds = new HashSet<>();
        terminateIds.add(802L);
        insuranceTerminateService.removeContractToInsuranceSession(terminateIds, 1L);
    }
}