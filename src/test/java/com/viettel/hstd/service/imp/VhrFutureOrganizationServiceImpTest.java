package com.viettel.hstd.service.imp;

import com.viettel.hstd.core.utils.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class VhrFutureOrganizationServiceImpTest {
    @Test
    @DisplayName("Test Divided Path Into Array")
    void testDividedPathIntoArray() {
        String path = "/148841/148842/165317/9004482/9023081/9023087/9023088/";
        assertEquals(StringUtils.dividedPathIntoArray(path, 4), "9004482");
        assertNull(StringUtils.dividedPathIntoArray(path, 9));
    }
}