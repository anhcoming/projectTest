package com.viettel.hstd.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenderTest {

    @Test
    @DisplayName("Test Gender Enum")
    void testGenderEnum() {
        assertEquals(Gender.FEMALE, Gender.of(2));
        assertEquals(Gender.UNKNOWN, Gender.of("0"));
    }
}