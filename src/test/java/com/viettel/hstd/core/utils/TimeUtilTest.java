package com.viettel.hstd.core.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilTest {

    @Test
    @DisplayName("Interval Between LocalDates")
    void intervalBetweenLocalDates() {
        LocalDate fromLocalDate = LocalDate.of(2015,10,20);
        LocalDate toLocalDate = LocalDate.of(2021,03,21);

        String interval = TimeUtil.intervalBetweenLocalDates(fromLocalDate, toLocalDate);

        assertEquals(interval, "5 năm 5 tháng 1 ngày");
    }

}