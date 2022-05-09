package com.viettel.hstd.entity.vps;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeVhrEntityTest {
    @Test
    @DisplayName("Test get/set Join company date")
    void testGetSetJoinCompanyDate() {
        EmployeeVhrEntity employeeVhrEntity = new EmployeeVhrEntity();

        employeeVhrEntity.setJoinCompanyDate(LocalDate.of(2021, 01, 01));
        LocalDate joinCompanyDate = employeeVhrEntity.getJoinCompanyDate();
        Assertions.assertTrue(employeeVhrEntity.getJoinCompanyDate().isEqual(LocalDate.of(2021, 01, 01)));
    }
}