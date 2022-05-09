package com.viettel.hstd.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Slf4j
public class TimeUtil {
    public static Timestamp truncLDTTo5Minute(LocalDateTime localDateTime) {
        int minute = localDateTime.getMinute();
        int round5Minute = Math.round(minute / 5) * 5;
        return Timestamp.from(localDateTime.withMinute(round5Minute).withSecond(0).withNano(0).atZone(ZoneId.systemDefault()).toInstant().truncatedTo(ChronoUnit.MINUTES));
    }

    public static Timestamp convertLDT2Timestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }

    public static java.sql.Date convertLD2Date(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    public static LocalDateTime convertStringEpochMilliSecondToLocalDateTime(String dateTimeString) {
        if (dateTimeString == null) return null;
        LocalDateTime result = null;
        try {
            if (dateTimeString.length() <= 3) {
                return null;
            }
            String epochMilliSecondString = dateTimeString.substring(dateTimeString.length() - 3);
            String epochSecondString = dateTimeString.substring(0, dateTimeString.length() - 3);
            result = LocalDateTime.ofEpochSecond(Long.parseLong(epochSecondString), Integer.parseInt(epochMilliSecondString), ZoneOffset.ofHours(7));
        } catch (NumberFormatException ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }
        return result;
    }

    public static String intervalBetweenLocalDates(LocalDate fromLocalDate, LocalDate toLocalDate) {
        String result = "";
        if (fromLocalDate == null || toLocalDate == null) return "Không xác định";
        LocalDate tempLocalDate = fromLocalDate;

        long years = tempLocalDate.until(toLocalDate, ChronoUnit.YEARS);
        tempLocalDate = tempLocalDate.plusYears(years);

        long months = tempLocalDate.until(toLocalDate, ChronoUnit.MONTHS);
        tempLocalDate = tempLocalDate.plusMonths(months);

        long days = tempLocalDate.until(toLocalDate, ChronoUnit.DAYS);
        tempLocalDate = tempLocalDate.plusDays(days);

        if (years > 0) result += years + " năm ";
        if (months > 0) result += months + " tháng ";
        if (days > 0) result += days + " ngày";

        if (years == 0 && months == 0 && days == 0) {
            result = "0 ngày";
        }
        return result;
    }
}
